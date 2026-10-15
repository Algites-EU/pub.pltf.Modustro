package eu.algites.pltf.modustro.builder.gradleinit;
import org.testng.annotations.Test;import static org.testng.Assert.*;import java.io.*;import java.util.*;
public class AItcPublicationDescriptorTest {
 @Test public void orderedCompleteTree()throws Exception{
  File fixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var values=AIcArtifactDirectoryMetadataResolverKt.AIcReadSimpleYamlScalars(fixture);
  var policies=AIcArtifactDirectoryMetadataResolverKt.AIcOutputPublicationsFromConfig(values,fixture);
  assertTrue(policies.get("java.native_develop_binaries").getSnapshot().AIcEffectivePublicationEnabled());
  assertFalse(policies.get("java.native_develop_sources").getSnapshot().AIcEffectivePublicationEnabled());
  assertTrue(policies.get("java.native_product_binaries").getSnapshot().AIcEffectivePublicationEnabled());
  var endpoint=policies.get("java.native_product_binaries").getSnapshot().getPublicationEndpoints().get("primary");
  assertEquals(endpoint.getPublications().size(),2);
  var extensions=(List<Map<String,Object>>)endpoint.getPublications().get(0).get("PublicationFinalizationActions");
  assertEquals(((List<?>)extensions.get(0).get("FinalizationActions")).size(),1);
  assertEquals(endpoint.AIcEffectivePublicationRetryCount(),2);
  assertEquals(endpoint.getMaxConcurrentPublications(), Integer.valueOf(3));
  assertTrue(endpoint.AIcEffectiveExecutionEnabled());
  assertEquals(endpoint.AIcEffectiveExecutionOrder(),-1);
 }

 /** Verifies that endpoint disablement remains separate from output publication enablement. */
 @Test public void executionDisabledEndpoint()throws Exception{
  File locFixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var locValues=new HashMap<>(AIcArtifactDirectoryMetadataResolverKt.AIcReadSimpleYamlScalars(locFixture));
  locValues.put("OutputPublications.0.Snapshot.PublicationEndpoints.0.ExecutionEnabled","false");
  var locPolicies=AIcArtifactDirectoryMetadataResolverKt.AIcOutputPublicationsFromConfig(locValues,locFixture);
  var locBranch=locPolicies.get("java.native_product_binaries").getSnapshot();
  assertTrue(locBranch.AIcEffectivePublicationEnabled());
  assertFalse(locBranch.getPublicationEndpoints().get("primary").AIcEffectiveExecutionEnabled());
 }

 /** Subscription endpoint concurrency is parsed independently of publication endpoint concurrency. */
 @Test public void subscriptionEndpointConcurrency() throws Exception {
  File locFixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var locValues=new HashMap<String,String>();
  locValues.put("InputSubscriptions.0.InputSelector", "native_product_binaries");
  locValues.put("InputSubscriptions.0.TechnologyKind", "java");
  locValues.put("InputSubscriptions.0.Subscriptions.0.Id", "repository");
  locValues.put("InputSubscriptions.0.Subscriptions.0.Visibility", "public");
  locValues.put("InputSubscriptions.0.Subscriptions.0.Enabled", "false");
  locValues.put("InputSubscriptions.0.Subscriptions.0.MaxConcurrentSubscriptions", "6");
  var locSubscriptions=AIcArtifactDirectoryMetadataResolverKt.AIcInputSubscriptionsFromConfig(locValues,locFixture);
  assertEquals(locSubscriptions.get("java.native_product_binaries").getSubscriptions().get("repository").getMaxConcurrentSubscriptions(), Integer.valueOf(6));
 }

 /** Reject nonpositive subscription endpoint concurrency. */
 @Test(expectedExceptions={IllegalArgumentException.class,IllegalStateException.class})
 public void nonpositiveSubscriptionConcurrencyRejected() throws Exception {
  File locFixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var locValues=new HashMap<String,String>();
  locValues.put("InputSubscriptions.0.InputSelector", "native_product_binaries");
  locValues.put("InputSubscriptions.0.TechnologyKind", "java");
  locValues.put("InputSubscriptions.0.Subscriptions.0.Id", "repository");
  locValues.put("InputSubscriptions.0.Subscriptions.0.MaxConcurrentSubscriptions", "0");
  AIcArtifactDirectoryMetadataResolverKt.AIcInputSubscriptionsFromConfig(locValues,locFixture);
 }

 /** Reject nonpositive publication endpoint concurrency. */
 @Test(expectedExceptions={IllegalArgumentException.class,IllegalStateException.class})
 public void nonpositivePublicationConcurrencyRejected() throws Exception {
  File locFixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var locValues=new HashMap<>(AIcArtifactDirectoryMetadataResolverKt.AIcReadSimpleYamlScalars(locFixture));
  locValues.put("OutputPublications.0.Snapshot.PublicationEndpoints.0.MaxConcurrentPublications", "0");
  AIcArtifactDirectoryMetadataResolverKt.AIcOutputPublicationsFromConfig(locValues,locFixture);
 }

 /** Rejects a legacy switch instead of silently enabling a previously disabled endpoint. */
 @Test(expectedExceptions={IllegalArgumentException.class,IllegalStateException.class})
 public void legacyEndpointSwitchRejected()throws Exception{
  File locFixture=new File(getClass().getResource("/publication-tree.yml").toURI());
  var locValues=new HashMap<>(AIcArtifactDirectoryMetadataResolverKt.AIcReadSimpleYamlScalars(locFixture));
  locValues.put("OutputPublications.0.Snapshot.PublicationEndpoints.0.Enabled","false");
  AIcArtifactDirectoryMetadataResolverKt.AIcOutputPublicationsFromConfig(locValues,locFixture);
 }
}
