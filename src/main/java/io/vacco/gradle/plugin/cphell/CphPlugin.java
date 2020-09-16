package io.vacco.gradle.plugin.cphell;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CphPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.getExtensions().create("classpathHell", CphPluginExtension.class);
    project.getTasks().create("checkClasspath", CphTask.class);
  }
}