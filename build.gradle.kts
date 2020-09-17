repositories { jcenter() }

plugins { `java-library`; jacoco; `maven-publish` }

group = "io.vacco"
version = "1.6.0"

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

val tokenFile = File(System.getProperty("user.home"), ".vaccoToken").readText()
val libraryDesc: String by project
val licenseName: String by project
val licenseUrl: String by project
val gitUrl: String by project
val siteUrl: String by project

configure<PublishingExtension> {
  publications {
    create<MavenPublication>("vaccoOss") {
      from(components["java"])
      pom {
        name.set(project.name)
        description.set(libraryDesc)
        url.set(siteUrl)
        licenses { license { name.set(licenseName); url.set(licenseUrl) } }
        developers {
          developer { id.set("vacco"); name.set("Vaccove Crana, LLC."); email.set("humans@vacco.io") }
        }
        scm { connection.set(gitUrl); developerConnection.set(gitUrl); url.set(siteUrl) }
      }
    }
  }
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
