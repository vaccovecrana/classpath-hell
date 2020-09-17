package io.vacco.gradle.plugin.cphell;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.*;

public class ChResourceIdx extends TreeMap<String, List<File>> {

  public boolean contentEquals(String resource, List<File> sources) {
    return sources.stream()
        .map(src -> ChIoUtil.getHashOfResource(src, resource))
        .collect(Collectors.toSet()).size() == 1;
  }

  public void addResourcesFromJarFile(File location) {
    try {
      ZipFile zf = new ZipFile(location);
      final Enumeration<?> e = zf.entries();
      while (e.hasMoreElements()) {
        final ZipEntry ze = (ZipEntry) e.nextElement();
        final String resourceFileName = ze.getName();
        if (!resourceFileName.endsWith("/")) {
          List<File> sources = computeIfAbsent(resourceFileName, fn -> new ArrayList<>());
          sources.add(location);
        }
      }
      zf.close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void add(File location) {
    if (location.isFile()) {
      if (location.getName().toLowerCase().endsWith(".jar") || location.getName().toLowerCase().endsWith(".zip")) {
        addResourcesFromJarFile(location);
      }
    } else if (location.isDirectory()) {
      for (File fileOrDir : Objects.requireNonNull(location.listFiles())) {
        add(fileOrDir);
      }
    }
  }

  public List<Map.Entry<String, List<File>>> getDuplicates(boolean suppressExact, Predicate<String> includeFilter) {
    return entrySet().stream()
        .filter(e -> includeFilter.test(e.getKey()))
        .filter(e -> e.getValue().size() > 1)
        .filter(e -> !suppressExact || !contentEquals(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }
}
