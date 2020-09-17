package io.vacco.gradle.plugin.cphell;

import org.gradle.api.artifacts.*;

import java.util.*;
import java.util.function.Predicate;

public interface ChPluginExtension {

  /** convenience common defaults that are not very interesting */
  default List<String> commonResourceExclusions() {
    return new ArrayList<>(
        Arrays.asList(
            "^rootdoc.txt\\$", "^about.html\\$",
            "^NOTICE\\$", "^LICENSE\\$", "^LICENSE.*.txt\\$",
            "^META-INF/.*", ".*/\\$", ".*com/sun/.*", ".*javax/annotation/.*"
        )
    );
  }

  /** Optional list of configurations to limit the scan to */
  void setConfigurationsToScan(List<Configuration> configurationsToScan);
  List<Configuration> getConfigurationsToScan();

  /** instances of a resource that have the same hash will be considered equivalent and not be reported */
  void setSuppressExactDupes(boolean suppressExactDupes);
  boolean isSuppressExactDupes();

  /** override to supply a list of artifacts to exclude from the check (assuming includeArtifact has not been overridden) */
  void setArtifactExclusions(List<String> artifactExclusions);
  List<String> getArtifactExclusions();

  /** override to provide an alternative list of resources to exclude from the check */
  void setResourceExclusions(List<String> resourceExclusions);
  List<String> getResourceExclusions();

  /** override to provide an alternative inclusion strategy to the default (exclude artifacts from black list) */
  void setIncludeArtifact(Predicate<ResolvedArtifact> includeArtifact);
  Predicate<ResolvedArtifact> getIncludeArtifact();

  /** override to provide an alternative inclusion strategy to the default (exclude resources from black list) */
  void setIncludeResource(Predicate<String> includeResource);
  Predicate<String> getIncludeResource();
}
