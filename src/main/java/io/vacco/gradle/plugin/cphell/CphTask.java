package io.vacco.gradle.plugin.cphell;

import org.gradle.api.*;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CphTask extends DefaultTask {

  public static void reportDuplicates(String configName, List<Map.Entry<String, List<File>>> dupes, Logger log) {
    dupes.forEach(e -> {
      log.warn("configuration '{}' contains duplicate resource: {}", configName, e.getKey());
      e.getValue().stream().sorted(Comparator.comparing(File::getAbsolutePath))
          .forEach(f -> log.warn(" found within artifact: {}", f.getName()));
    });
  }

  @TaskAction public void action() {

    boolean hadDupes = false;
    CphPluginExtension ext = Objects.requireNonNull(getProject().getExtensions().findByType(CphPluginExtension.class));
    List<Configuration> configurations = ext.configurationsToScan.isEmpty() ? new ArrayList<>(getProject().getConfigurations()) : ext.configurationsToScan;
    List<Configuration> resolvedConfs = configurations.stream().filter(Configuration::isCanBeResolved).collect(Collectors.toList());

    for (Configuration conf : resolvedConfs) {
      getLogger().info("classpathHell: checking configuration : '{}'", conf.getName());

      CphResourceIdx idx = new CphResourceIdx();

      for (ResolvedArtifact art : conf.getResolvedConfiguration().getResolvedArtifacts()) {
        if (ext.includeArtifact.test(art)) {
          if (getLogger().isDebugEnabled()) {
            getLogger().debug("including artifact <{}>", art.getModuleVersion().getId());
          }
          idx.add(art.getFile());
        } else if (getLogger().isDebugEnabled()) {
          getLogger().debug("excluding artifact <{}>", art.getModuleVersion().getId());
        }
      }

      List<Map.Entry<String, List<File>>> dupes = idx.getDuplicates(ext.suppressExactDupes, ext.includeResource);
      reportDuplicates(conf.getName(), dupes, getLogger());

      if (!dupes.isEmpty()) { hadDupes = true; }
    }

    if (hadDupes) throw new GradleException("Duplicate resources detected");
  }
}