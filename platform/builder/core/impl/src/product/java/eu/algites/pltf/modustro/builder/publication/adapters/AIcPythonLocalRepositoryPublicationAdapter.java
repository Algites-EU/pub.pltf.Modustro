package eu.algites.pltf.modustro.builder.publication.adapters;

import eu.algites.pltf.modustro.builder.model.publication.AIcPublicationEndpoint;
import eu.algites.pltf.modustro.builder.model.publication.AIcPublicationPayload;
import eu.algites.pltf.modustro.builder.model.publication.AIcPublicationPayloadFile;
import eu.algites.pltf.modustro.builder.publication.AIcPublicationAttemptContext;
import eu.algites.pltf.modustro.builder.publication.AIiPublicationAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Publishes native Python distributions into a static, local PEP 503 Simple Repository API index.
 *
 * <p>Unlike {@link AIcPythonRepositoryPublicationAdapter}, this adapter requires no upload server,
 * Twine, credentials or Internet connection. The endpoint URI identifies the Python repository
 * root containing {@code packages/} and {@code simple/}; it must not identify the {@code simple/}
 * directory itself. SHA-256 fragments bind Simple API links to the published bytes. The index
 * is protected by an exclusive filesystem lock and pages are replaced atomically, so concurrent
 * Builder processes cannot discard one another's index entries.</p>
 */
public final class AIcPythonLocalRepositoryPublicationAdapter implements AIiPublicationAdapter {
    /** Identifies the local, file-backed Python publication adapter in the catalog. */
    public static final String ADAPTER_ID = "python-local-repository";
    private static final Pattern WHEEL = Pattern.compile("^([A-Za-z0-9_]+)-([A-Za-z0-9_.+!]+)-(?:[A-Za-z0-9_]+-)?[A-Za-z0-9_.]+-[A-Za-z0-9_.]+-[A-Za-z0-9_.]+\\.whl$");
    private static final Pattern SDIST = Pattern.compile("^([A-Za-z0-9_.-]+)-([0-9][A-Za-z0-9_.+!]*)\\.tar\\.gz$");

    /** Returns the adapter identity used by OutputPublications endpoint declarations. */
    @Override
    public String adapterId() {
        return ADAPTER_ID;
    }

    /**
     * Permits safe retries because an identical package filename may be republished byte-for-byte,
     * while conflicting changes to an existing filename are rejected rather than silently replaced.
     */
    @Override
    public boolean isRetrySafe(AIcPublicationPayload aPayload, AIcPublicationEndpoint aEndpoint) {
        return true;
    }

    /**
     * Copies a validated publication payload to the local package store, then rebuilds the Simple
     * index under a filesystem lock. An existing package with different content is rejected.
     *
     * @throws IllegalArgumentException if the endpoint or distribution naming is invalid
     * @throws IOException if the filesystem or a publication hash check fails
     */
    @Override
    public void publish(AIcPublicationAttemptContext aContext) throws Exception {
        Path locRoot = AIcRoot(aContext.endpoint().publicationUri());
        Path locPackages = locRoot.resolve("packages");
        Path locIndex = locRoot.resolve("simple");
        Files.createDirectories(locPackages);
        Files.createDirectories(locIndex);
        if (aContext.payload().files().isEmpty()) {
            throw new IllegalArgumentException("python-local-repository requires at least one wheel or sdist");
        }
        aContext.progressReporter().started("Publishing Python distributions to " + locRoot);
        try (FileChannel locLockChannel = FileChannel.open(locRoot.resolve(".modustro-local-index.lock"),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             java.nio.channels.FileLock locLock = locLockChannel.lock()) {
            for (AIcPublicationPayloadFile locEntry : aContext.payload().files()) {
                if (aContext.cancellationToken().isCancellationRequested()) {
                    throw new InterruptedException("Python local publication cancelled");
                }
                Path locSource = locEntry.path();
                String locFileName = locSource.getFileName().toString();
                AIcCoordinates(locFileName);
                Path locDestination = locPackages.resolve(locFileName);
                String locChecksum = AIcSha256(locSource);
                if (Files.exists(locDestination)) {
                    if (!AIcSha256(locDestination).equals(locChecksum)) {
                        throw new IOException("Immutable Python distribution already exists with different bytes: " + locDestination);
                    }
                    continue;
                }
                Path locTemporary = Files.createTempFile(locPackages, ".modustro-", ".tmp");
                try {
                    Files.copy(locSource, locTemporary, StandardCopyOption.REPLACE_EXISTING);
                    if (!AIcSha256(locTemporary).equals(locChecksum)) {
                        throw new IOException("Python distribution checksum changed during local publication: " + locSource);
                    }
                    AIcReplace(locTemporary, locDestination);
                } finally {
                    Files.deleteIfExists(locTemporary);
                }
            }
            Map<String, List<Path>> locProjects = new TreeMap<>();
            try (Stream<Path> locFiles = Files.list(locPackages)) {
                for (Path locFile : locFiles.filter(Files::isRegularFile).toList()) {
                    String locFileName = locFile.getFileName().toString();
                    if (!locFileName.endsWith(".whl") && !locFileName.endsWith(".tar.gz")) continue;
                    String locName = AIcCoordinates(locFileName).name();
                    locProjects.computeIfAbsent(locName, ignored -> new ArrayList<>()).add(locFile);
                }
            }
            StringBuilder locRootPage = new StringBuilder("<!DOCTYPE html>\n<html><body>\n");
            for (Map.Entry<String, List<Path>> locProject : locProjects.entrySet()) {
                String locName = locProject.getKey();
                locRootPage.append("<a href=\"").append(locName).append("/\">")
                        .append(locName).append("</a><br>\n");
                StringBuilder locProjectPage = new StringBuilder("<!DOCTYPE html>\n<html><body>\n");
                locProject.getValue().sort(Path::compareTo);
                for (Path locFile : locProject.getValue()) {
                    String locNameOfFile = locFile.getFileName().toString();
                    locProjectPage.append("<a href=\"../../packages/")
                            .append(URLEncoder.encode(locNameOfFile, StandardCharsets.UTF_8).replace("+", "%20"))
                            .append("#sha256=").append(AIcSha256(locFile)).append("\">")
                            .append(locNameOfFile).append("</a><br>\n");
                }
                AIcWriteAtomic(locIndex.resolve(locName).resolve("index.html"), locProjectPage.append("</body></html>\n").toString());
            }
            AIcWriteAtomic(locIndex.resolve("index.html"), locRootPage.append("</body></html>\n").toString());
        }
        aContext.progressReporter().completed("Python local Simple index publication completed: " + locIndex);
    }

    private record AIcdNameAndVersion(String name, String version) { }

    private static AIcdNameAndVersion AIcCoordinates(String aFileName) {
        Matcher locMatcher = aFileName.endsWith(".whl") ? WHEEL.matcher(aFileName) : SDIST.matcher(aFileName);
        if (!locMatcher.matches()) throw new IllegalArgumentException("Invalid Python distribution filename: " + aFileName);
        return new AIcdNameAndVersion(locMatcher.group(1).replaceAll("[-_.]+", "-").toLowerCase(java.util.Locale.ROOT),
                locMatcher.group(2));
    }

    private static Path AIcRoot(URI aUri) {
        if (aUri == null) throw new IllegalArgumentException("python-local-repository requires PublicationUri");
        if (aUri.getScheme() == null) return Path.of(aUri.toString()).toAbsolutePath().normalize();
        if (!"file".equalsIgnoreCase(aUri.getScheme())) {
            throw new IllegalArgumentException("python-local-repository accepts only file: PublicationUri");
        }
        return Path.of(aUri).toAbsolutePath().normalize();
    }

    private static String AIcSha256(Path aFile) throws IOException {
        try {
            MessageDigest locDigest = MessageDigest.getInstance("SHA-256");
            try (java.io.InputStream locStream = Files.newInputStream(aFile)) {
                byte[] locBuffer = new byte[8192];
                for (int locCount; (locCount = locStream.read(locBuffer)) != -1; ) {
                    locDigest.update(locBuffer, 0, locCount);
                }
            }
            return HexFormat.of().formatHex(locDigest.digest());
        } catch (java.security.NoSuchAlgorithmException locError) {
            throw new IllegalStateException("SHA-256 is required by the Java runtime", locError);
        }
    }

    private static void AIcWriteAtomic(Path aTarget, String aText) throws IOException {
        Files.createDirectories(aTarget.getParent());
        Path locTemporary = Files.createTempFile(aTarget.getParent(), ".modustro-", ".tmp");
        try {
            Files.writeString(locTemporary, aText, StandardCharsets.UTF_8);
            AIcReplace(locTemporary, aTarget);
        } finally {
            Files.deleteIfExists(locTemporary);
        }
    }

    private static void AIcReplace(Path aSource, Path aDestination) throws IOException {
        try {
            Files.move(aSource, aDestination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException locIgnored) {
            Files.move(aSource, aDestination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
