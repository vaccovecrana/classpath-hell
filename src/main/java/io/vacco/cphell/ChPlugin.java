package io.vacco.cphell;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ChPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project.getExtensions().create(ChPluginExtension.class, "classpathHell", ChDefaultPluginExtension.class);
    project.getTasks().create("checkClasspath", ChTask.class);
  }
}