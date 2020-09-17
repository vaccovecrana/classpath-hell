package io.vacco.gradle.plugin.cphell;

import org.gradle.api.artifacts.*;

import java.util.*;
import java.util.function.Predicate;

public class CphPluginExtension {

  public static List<String> commonResourceExclusions() { // convenience common defaults that are not very interesting
    return Arrays.asList(
        "^rootdoc.txt\\$", "^about.html\\$",
        "^NOTICE\\$", "^LICENSE\\$", "^LICENSE.*.txt\\$",
        "^META-INF/.*", ".*/\\$", ".*com/sun/.*", ".*javax/annotation/.*"
    );
  }

  public List<String> artifactExclusions = new ArrayList<>(); // override to supply a list of artifacts to exclude from the check (assuming includeArtifact has not been overridden)
  public List<Configuration> configurationsToScan = new ArrayList<>(); // optional list of configurations to limit the scan to
  public List<String> resourceExclusions = new ArrayList<>(); // override to provide an alternative list of resources to exclude from the check
  public boolean suppressExactDupes = false; // instances of a resource that have the same hash will be considered equivalent and not be reported

  // override to provide an alternative inclusion strategy to the default (exclude artifacts from black list)
  public Predicate<ResolvedArtifact> includeArtifact = f -> excludeArtifactPaths(artifactExclusions, f);
  // override to provide an alternative inclusion strategy to the default (exclude resources from black list)
  public Predicate<String> includeResource = f -> excludeMatches(resourceExclusions, f);

  protected static boolean excludeArtifactPaths(List<String> excludedPatterns, ResolvedArtifact f) {
    boolean inc = true;
    for (String ex : excludedPatterns) {
      if (f.getFile().getAbsolutePath().matches(ex)) {
        inc = false;
      }
    }
    return inc;
  }

  protected static boolean excludeMatches(List<String> excludedPatterns, String f) {
    boolean inc = true;
    for (String ex : excludedPatterns) {
      if (f.matches(ex)) {
        inc = false;
        break;
      }
    }
    return inc;
  }
}
