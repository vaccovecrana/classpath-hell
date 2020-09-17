package io.vacco.gradle.plugin.cphell;

import org.gradle.api.*;
import org.gradle.api.artifacts.*;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CphTask extends DefaultTask {

  public List<String> commonResourceExclusions() { // convenience common defaults that are not very interesting
    return new ArrayList<>(
        Arrays.asList(
            "^rootdoc.txt\\$", "^about.html\\$",
            "^NOTICE\\$", "^LICENSE\\$", "^LICENSE.*.txt\\$",
            "^META-INF/.*", ".*/\\$", ".*com/sun/.*", ".*javax/annotation/.*"
        )
    );
  }

  public List<Configuration> configurationsToScan = new ArrayList<>(); // optional list of configurations to limit the scan to

  public boolean suppressExactDupes = false; // instances of a resource that have the same hash will be considered equivalent and not be reported
  public List<String> artifactExclusions = new ArrayList<>(); // override to supply a list of artifacts to exclude from the check (assuming includeArtifact has not been overridden)
  public List<String> resourceExclusions = new ArrayList<>(); // override to provide an alternative list of resources to exclude from the check

  // override to provide an alternative inclusion strategy to the default (exclude artifacts from black list)
  public Predicate<ResolvedArtifact> includeArtifact = CphTaskUtil.defaultArtifactInclude(artifactExclusions);
  // override to provide an alternative inclusion strategy to the default (exclude resources from black list)
  public Predicate<String> includeResource = CphTaskUtil.defaultResourceInclude(resourceExclusions);

  public static void reportDuplicates(String configName, List<Map.Entry<String, List<File>>> dupes, Logger log) {
    dupes.forEach(e -> {
      log.warn("configuration '{}' contains duplicate resource: {}", configName, e.getKey());
      e.getValue().stream().sorted(Comparator.comparing(File::getAbsolutePath))
          .forEach(f -> log.warn(" found within artifact: {}", f.getName()));
    });
  }

  @TaskAction public void action() {

    org.gradle.api.logging.Logger log = Logging.getLogger(CphTask.class);

    boolean hadDupes = false;
    List<Configuration> configurations = configurationsToScan.isEmpty() ? new ArrayList<>(getProject().getConfigurations()) : configurationsToScan;
    List<Configuration> resolvedConfs = configurations.stream().filter(Configuration::isCanBeResolved).collect(Collectors.toList());

    for (Configuration conf : resolvedConfs) {
      log.info("classpathHell: checking configuration : '{}'", conf.getName());

      CphResourceIdx idx = new CphResourceIdx();

      for (ResolvedArtifact art : conf.getResolvedConfiguration().getResolvedArtifacts()) {
        if (includeArtifact.test(art)) {
          if (log.isDebugEnabled()) {
            log.debug("including artifact <{}>", art.getModuleVersion().getId());
          }
          idx.add(art.getFile());
        } else if (log.isDebugEnabled()) {
          log.debug("excluding artifact <{}>", art.getModuleVersion().getId());
        }
      }

      List<Map.Entry<String, List<File>>> dupes = idx.getDuplicates(suppressExactDupes, includeResource);
      reportDuplicates(conf.getName(), dupes, log);

      if (!dupes.isEmpty()) { hadDupes = true; }
    }

    if (hadDupes) throw new GradleException("Duplicate resources detected");
  }
}