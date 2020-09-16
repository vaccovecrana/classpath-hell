buildscript {
  repositories { maven { name = "VaccoOss"; setUrl("https://dl.bintray.com/vaccovecrana/vacco-oss") } }
  dependencies { classpath("io.vacco.common:common-build:0.1.0") }
}
plugins { `java-gradle-plugin` }
apply(from = project.buildscript.classLoader.getResource("io/vacco/common/java-library.gradle.kts").toURI())

group = "io.vacco"
version = "1.6.0"

gradlePlugin {
  plugins {
    create("classpath-hell-gradle-plugin") {
      id = "io.vacco.classpath-hell-gradle-plugin"
      implementationClass = "io.vacco.gradle.plugin.cphell.ClasspathHellPlugin"
    }
  }
}

configure<JavaPluginExtension> {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
