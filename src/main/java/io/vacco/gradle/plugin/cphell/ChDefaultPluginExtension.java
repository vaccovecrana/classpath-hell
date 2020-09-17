package io.vacco.gradle.plugin.cphell;

import org.gradle.api.artifacts.*;

import java.util.*;
import java.util.function.Predicate;

public class ChDefaultPluginExtension implements ChPluginExtension {

  private List<Configuration> configurationsToScan = new ArrayList<>();
  private boolean suppressExactDupes = false;
  private List<String> artifactExclusions = new ArrayList<>();
  private List<String> resourceExclusions = new ArrayList<>();
  private Predicate<ResolvedArtifact> includeArtifact = a -> defaultArtifactInclude(a, artifactExclusions);
  private Predicate<String> includeResource = r -> defaultResourceInclude(r, resourceExclusions);

  public static boolean defaultArtifactInclude(ResolvedArtifact art, List<String> excludedPatterns) {
    boolean inc = true;
    for (String ex : excludedPatterns) {
      if (art.getFile().getAbsolutePath().matches(ex)) {
        inc = false;
      }
    }
    return inc;
  }

  public static boolean defaultResourceInclude(String resource, List<String> excludedPatterns) {
    boolean inc = true;
    for (String ex : excludedPatterns) {
      if (resource.matches(ex)) {
        inc = false;
        break;
      }
    }
    return inc;
  }

  @Override public List<Configuration> getConfigurationsToScan() { return configurationsToScan; }
  @Override public void setConfigurationsToScan(List<Configuration> configurationsToScan) {
    this.configurationsToScan = configurationsToScan;
  }

  @Override public boolean isSuppressExactDupes() { return suppressExactDupes; }
  @Override public void setSuppressExactDupes(boolean suppressExactDupes) {
    this.suppressExactDupes = suppressExactDupes;
  }

  @Override public List<String> getArtifactExclusions() { return artifactExclusions; }
  @Override public void setArtifactExclusions(List<String> artifactExclusions) {
    this.artifactExclusions = artifactExclusions;
  }

  @Override public List<String> getResourceExclusions() { return resourceExclusions; }
  @Override public void setResourceExclusions(List<String> resourceExclusions) {
    this.resourceExclusions = resourceExclusions;
  }

  @Override public Predicate<ResolvedArtifact> getIncludeArtifact() { return includeArtifact; }
  @Override public void setIncludeArtifact(Predicate<ResolvedArtifact> includeArtifact) {
    this.includeArtifact = includeArtifact;
  }

  @Override public Predicate<String> getIncludeResource() { return includeResource; }
  @Override public void setIncludeResource(Predicate<String> includeResource) {
    this.includeResource = includeResource;
  }
}
