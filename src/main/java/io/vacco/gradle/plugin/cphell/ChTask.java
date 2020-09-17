package io.vacco.gradle.plugin.cphell;

import org.gradle.api.*;
import org.gradle.api.artifacts.*;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ChTask extends DefaultTask {

  public static void reportDuplicates(String configName, List<Map.Entry<String, List<File>>> dupes, Logger log) {
    dupes.forEach(e -> {
      log.warn("configuration '{}' contains duplicate resource: {}", configName, e.getKey());
      e.getValue().stream().sorted(Comparator.comparing(File::getAbsolutePath))
          .forEach(f -> log.warn(" found within artifact: {}", f.getName()));
    });
  }

  @TaskAction public void action() {

    org.gradle.api.logging.Logger log = Logging.getLogger(ChTask.class);
    ChPluginExtension ext = getProject().getExtensions().getByType(ChPluginExtension.class);

    boolean hadDupes = false;
    List<Configuration> configurations = ext.getConfigurationsToScan().isEmpty() ? new ArrayList<>(getProject().getConfigurations()) : ext.getConfigurationsToScan();
    List<Configuration> resolvedConfs = configurations.stream().filter(Configuration::isCanBeResolved).collect(Collectors.toList());

    if (!ext.getArtifactExclusions().isEmpty()) { log.info("artifactExclusions: {}", ext.getArtifactExclusions()); }
    if (!ext.getResourceExclusions().isEmpty()) { log.info("resourceExclusions: {}", ext.getResourceExclusions()); }

    for (Configuration conf : resolvedConfs) {
      log.info("checking configuration : '{}'", conf.getName());
      ChResourceIdx idx = new ChResourceIdx();

      for (ResolvedArtifact art : conf.getResolvedConfiguration().getResolvedArtifacts()) {
        if (ext.getIncludeArtifact().test(art)) {
          log.info("including artifact <{}>", art.getModuleVersion().getId());
          idx.add(art.getFile());
        } else {
          log.info("excluding artifact <{}>", art.getModuleVersion().getId());
        }
      }

      List<Map.Entry<String, List<File>>> dupes = idx.getDuplicates(ext.isSuppressExactDupes(), ext.getIncludeResource());
      reportDuplicates(conf.getName(), dupes, log);

      if (!dupes.isEmpty()) { hadDupes = true; }
    }

    if (hadDupes) throw new GradleException("Duplicate resources detected");
  }
}