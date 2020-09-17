package io.vacco.gradle.plugin.cphell;

import org.gradle.api.artifacts.ResolvedArtifact;

import java.util.*;
import java.util.function.Predicate;

public class CphTaskUtil {

  public static final List<String> commonExcludes = new ArrayList<>(
      Arrays.asList(
          "^rootdoc.txt\\$", "^about.html\\$",
          "^NOTICE\\$", "^LICENSE\\$", "^LICENSE.*.txt\\$",
          "^META-INF/.*", ".*/\\$", ".*com/sun/.*", ".*javax/annotation/.*"
      )
  );

  public static Predicate<String> defaultResourceInclude(List<String> excludedPatterns) {
    return f -> {
      boolean inc = true;
      for (String ex : excludedPatterns) {
        if (f.matches(ex)) {
          inc = false;
          break;
        }
      }
      return inc;
    };
  }

  public static Predicate<ResolvedArtifact> defaultArtifactInclude(List<String> excludedPatterns) {
    return f -> {
      boolean inc = true;
      for (String ex : excludedPatterns) {
        if (f.getFile().getAbsolutePath().matches(ex)) {
          inc = false;
        }
      }
      return inc;
    };
  }
}
