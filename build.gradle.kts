plugins { `java-gradle-plugin`; jacoco; `maven-publish` }

repositories { jcenter() }

group = "io.vacco"
version = "1.7.0"

dependencies {
  api(gradleApi())
  testImplementation("io.github.j8spec:j8spec:3.0.0")
  testRuntimeOnly("org.hamcrest:hamcrest-all:1.3")
  testRuntimeOnly("org.hamcrest:hamcrest-core:1.3")
}

configure<JavaPluginExtension> {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<JavaCompile> { options.compilerArgs.add("-Xlint:all") }
tasks.withType<Test> { this.testLogging { this.showStandardStreams = true } }

gradlePlugin {
  plugins {
    create("simplePlugin") {
      id = "io.vacco.classpath-hell-gradle-plugin"
      implementationClass = "io.vacco.cphell.ChPlugin"
    }
  }
}

val tokenFile = File(System.getProperty("user.home"), ".vaccoToken").readText()

configure<PublishingExtension> {
  repositories {
    maven {
      name = "VaccoOss"
      setUrl("https://api.bintray.com/maven/vaccovecrana/vacco-oss/${project.name}")
      credentials {
        username = tokenFile.split(":")[0].trim()
        password = tokenFile.split(":")[1].trim()
      }
    }
  }
}
