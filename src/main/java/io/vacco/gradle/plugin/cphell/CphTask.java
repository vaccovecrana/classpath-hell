package io.vacco.gradle.plugin.cphell;

import org.gradle.api.*;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CphTask extends DefaultTask {

  private static String gav(ResolvedDependency d) {
    ModuleVersionIdentifier f = d.getModule().getId();
    return String.format("%s:%s:%s", f.getGroup(), f.getName(), f.getVersion());
  }

  private static Set<String> findDep(List<String> pathAccumulator, ResolvedDependency dep, ResolvedArtifact source) {
    if (dep.getModule().getId() == source.getModuleVersion().getId()) {
      List<String> path = new ArrayList<>(pathAccumulator);
      Collections.reverse(path);
      Set<String> s = new HashSet<>();
      s.add(String.join(" <- ", path));
      return s;
    } else {
      Set<ResolvedDependency> children = dep.getChildren();
      return findDepC(pathAccumulator, children, source);
    }
  }

  private static Set<String> findDepC(List<String> pathAccumulator, Set<ResolvedDependency> children, ResolvedArtifact source) {
    Set<String> found = new HashSet<>();
    for (ResolvedDependency child : children) {
      List<String> newAccum = new ArrayList<>(pathAccumulator);
      newAccum.add(gav(child));
      Set<String> f = findDep(newAccum, child, source);
      found.addAll(f);
    }
    return found;
  }

  private static Set<String> findRoute(Configuration conf, ResolvedArtifact source) {
    Set<ResolvedDependency> deps = conf.getResolvedConfiguration().getFirstLevelModuleDependencies();
    return findDepC(new ArrayList<>(), deps, source);
  }

  private Set<ResolvedArtifact> suppressPermittedCombinations(boolean suppressByHash, String resourcePath, Set<ResolvedArtifact> dupes) {
    if (!suppressByHash) return dupes;
    Set<String> hashes = new HashSet<>();
    Set<String> ids = new HashSet<>();

    for (ResolvedArtifact file : dupes) {
      String md5 = CphIoUtil.getHashOfResource(file.getFile(), resourcePath);
      if (getLogger().isDebugEnabled()) {
        getLogger().debug("    {}, md5 {} @ {}", resourcePath, md5, file.getId().getComponentIdentifier());
      }
      hashes.add(md5);
      ids.add(file.getId().getComponentIdentifier().toString());
    }
    if (hashes.size() == 1) {
      if (getLogger().isDebugEnabled()) {
        getLogger().debug("    {} has been automatically suppressed across [{}]", resourcePath, ids);
      }
      return new HashSet<>();
    }
    return dupes;
  }

  @TaskAction public void action() {

    boolean[] hadDupes = new boolean[] {false};
    CphPluginExtension ext = Objects.requireNonNull(getProject().getExtensions().findByType(CphPluginExtension.class));
    List<Configuration> configurations = ext.configurationsToScan.isEmpty() ? new ArrayList<>(getProject().getConfigurations()) : ext.configurationsToScan;
    List<Configuration> resolvedConfs = configurations.stream().filter(Configuration::isCanBeResolved).collect(Collectors.toList());

    for (Configuration conf : resolvedConfs) {
      getLogger().info("classpathHell: checking configuration : '{}'", conf.getName());
      Map<String, Set<ResolvedArtifact>> resourceToSource = new HashMap<>();

      for (ResolvedArtifact resolvedArtifact : conf.getResolvedConfiguration().getResolvedArtifacts()) {
        if (ext.includeArtifact.test(resolvedArtifact)) {
          if (getLogger().isDebugEnabled()) {
            getLogger().debug("including artifact <{}>", resolvedArtifact.getModuleVersion().getId());
          }
          File file = resolvedArtifact.getFile();
          List<String> resourcesInFile = CphIoUtil.getResources(file, getLogger());
          List<String> includedResources = resourcesInFile.stream().filter(res -> {
            boolean inc = ext.includeResource.test(res);
            if (getLogger().isDebugEnabled()) {
              getLogger().debug("{} resource <{}>", inc ? "including" : "excluding", res);
            }
            return inc;
          }).collect(Collectors.toList());

          for (String res : includedResources) {
            Set<ResolvedArtifact> sources = resourceToSource.get(res);
            if (!resourceToSource.containsKey(res)) {
              sources = new HashSet<>();
              resourceToSource.put(res, sources);
            }
            sources.add(resolvedArtifact);
          }
        } else if (getLogger().isDebugEnabled()) {
          getLogger().debug("excluding artifact <{}>", resolvedArtifact.getModuleVersion().getId());
        }
      }

      resourceToSource.forEach((resourcePath, sources) -> {
        if (getLogger().isDebugEnabled()) {
          getLogger().debug("checking resource: {}", resourcePath);
        }
        if (sources.size() > 1) {
          Set<ResolvedArtifact> dupes = suppressPermittedCombinations(ext.suppressExactDupes, resourcePath, sources);
          boolean thisHasDupes = !dupes.isEmpty();
          if (thisHasDupes) {
            getLogger().info("configuration '{}' contains duplicate resource: {}", conf.getName(), resourcePath);
            dupes.stream()
                .sorted(Comparator.comparing(ra0 -> ra0.getId().getDisplayName()))
                .forEach(source -> {
                  getLogger().info(" found within dependency: {}", source.getModuleVersion().getId());
                  findRoute(conf, source).stream().sorted()
                      .forEach(route -> getLogger().info("  imported via: {}", route));
            });
          }
          if (thisHasDupes) { hadDupes[0] = true; }
        }
      });
    }

    if (hadDupes[0]) throw new GradleException("Duplicate resources detected");
  }
}