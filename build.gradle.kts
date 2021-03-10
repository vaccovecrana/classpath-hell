plugins { `java-library`; jacoco; `maven-publish`; signing }

repositories { mavenCentral() }

group = "io.vacco"
version = "1.8.0"

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

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }
      pom {
        name.set("Classpath Hell Gradle Plugin")
        description.set("Gradle plugin, breaks the build on class path collisions")
        url.set("https://github.com/vaccovecrana/classpath-hell-gradle-plugin")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/vaccovecrana/classpath-hell-gradle-plugin/blob/master/LICENSE")
          }
        }
        developers {
          developer {
            id.set("vacco")
            name.set("Vaccove Crana, LLC.")
            email.set("humans@vacco.io")
          }
        }
        scm {
          connection.set("https://github.com/vaccovecrana/classpath-hell-gradle-plugin.git")
          developerConnection.set("https://github.com/vaccovecrana/classpath-hell-gradle-plugin.git")
          url.set("https://github.com/vaccovecrana/classpath-hell-gradle-plugin.git")
        }
      }
    }
  }
  repositories {
    maven {
      name = "SonatypeOSS"
      setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = System.getenv("SONATYPE_USER")
        password = System.getenv("SONATYPE_PASSWORD")
      }
    }
  }
}

signing {
  sign(publishing.publications["mavenJava"])
  useInMemoryPgpKeys(System.getenv("MAVEN_SIGNING_PRV"), "")
}
