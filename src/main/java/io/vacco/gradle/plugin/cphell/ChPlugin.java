package io.vacco.gradle.plugin.cphell;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ChPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project.getExtensions().create("classpathHell", ChPluginExtension.class);
    project.getTasks().create("checkClasspath", ChTask.class);
  }
}