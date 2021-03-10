package io.vacco.cphell;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class ChPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project.getExtensions().create(ChPluginExtension.class, "classpathHell", ChDefaultPluginExtension.class);
    Task chTask = project.getTasks().create("checkClasspath", ChTask.class);
    chTask.setGroup("build");
    chTask.setDescription("Check for classpath duplicates");
  }
}