buildscript {
  repositories { maven { name = "VaccoOss"; setUrl("https://dl.bintray.com/vaccovecrana/vacco-oss") } }
  dependencies { classpath("io.vacco.common:common-build:0.1.0") }
}

apply(from = project.buildscript.classLoader.getResource("io/vacco/common/java-library.gradle.kts").toURI())

group = "io.vacco"
version = "1.6.0"

configure<JavaPluginExtension> {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

val api by configurations
val testRuntime by configurations
dependencies {
  api(gradleApi())
  testRuntime("org.hamcrest:hamcrest-all:1.3")
  testRuntime("org.hamcrest:hamcrest-core:1.3")
}