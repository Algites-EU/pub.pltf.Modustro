package eu.algites.pltf.modustro.builder.publication;

import eu.algites.pltf.modustro.builder.model.execution.AIngBuildExecutionFailurePolicy_1;
import eu.algites.pltf.modustro.builder.model.publication.AIcPublicationEndpoint;
import eu.algites.pltf.modustro.builder.model.subscription.AIcInputSubscription;
import eu.algites.pltf.modustro.builder.subscription.AIcInputSubscriptionResolver;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Validates optional per-endpoint concurrency settings without claiming scheduler enforcement. */
public class AItcEndpointConcurrencyConfigurationTest {

    /** Explicit publication limits survive endpoint parsing and cloning. */
    @Test
    public void publicationLimitIsPreserved() {
        Map<String, Object> locDeclaration = Map.of(
                "PublicationUri", "https://example.invalid/repository/",
                "PublicationAdapter", "maven-repository",
                "MaxConcurrentPublications", 3);
        AIcPublicationEndpoint locEndpoint = AIcPublicationPlanner.endpoint(locDeclaration, "primary");
        Assert.assertEquals(locEndpoint.maxConcurrentPublications(), Integer.valueOf(3));
        Assert.assertEquals(AIcPublicationPlanner.endpoint(Map.of(
                "PublicationUri", "https://example.invalid/repository/",
                "PublicationAdapter", "maven-repository"), "unlimited").maxConcurrentPublications(), null);
    }

    /** Nonpositive publication and subscription limits are invalid. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nonpositivePublicationLimitRejected() {
        AIcPublicationPlanner.endpoint(Map.of(
                "PublicationUri", "https://example.invalid/repository/",
                "PublicationAdapter", "maven-repository",
                "MaxConcurrentPublications", 0), "primary");
    }

    /** Subscription limits are independent of publication limits and belong to the subscription endpoint. */
    @Test
    public void subscriptionLimitFollowsOverride() {
        AIcInputSubscription locBase = subscription(2);
        AIcInputSubscription locOverride = subscription(5);
        AIcInputSubscription locMerged = new AIcInputSubscriptionResolver()
                .merge(List.of(locBase), List.of(locOverride)).get(0);
        Assert.assertEquals(locMerged.maxConcurrentSubscriptions(), Integer.valueOf(5));
    }

    /** A missing limit remains unbounded after an endpoint override. */
    @Test
    public void subscriptionLimitSurvivesUnspecifiedOverride() {
        AIcInputSubscription locMerged = new AIcInputSubscriptionResolver()
                .merge(List.of(subscription(2)), List.of(subscription(null))).get(0);
        Assert.assertEquals(locMerged.maxConcurrentSubscriptions(), Integer.valueOf(2));
    }

    /** Nonpositive subscription limits are rejected in the effective model. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nonpositiveSubscriptionLimitRejected() {
        subscription(0);
    }

    private static AIcInputSubscription subscription(Integer aLimit) {
        return new AIcInputSubscription("java", "native_product_binaries", "repository", false,
                "public", "release", URI.create("https://example.invalid/releases/"),
                "maven-repository", null, 0, aLimit, Map.of());
    }
}
