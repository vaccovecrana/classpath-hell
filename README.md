# classpathHell - Classpath Mayhem Detector

Statically typed implementation of [portingle/classpathHell](https://github.com/portingle/classpathHell)

It's far too easy to end up with multiple copies of a class or resource on your classpath leading to
runtime errors that, due to classpath ordering instability, might not show up until late in your release cycle,
or possibly even production. 

`classpathHell` is a gradle plugin that breaks the build if there are classpath collisions.

## Configuration

```groovy
classpathHell {
  configurationsToScan = [configurations.runtime]
  suppressExactDupes = true
  resourceExclusions = ["somePath/", ".*class"] // pattern matches on resource path
  resourceExclusions.addAll([".*/", "anotherPath/.*"])
  artifactExclusions = [".*hamcrest-core.*"]
}
```

```
./gradlew checkClasspath
```

```groovy
build.dependsOn(["checkClasspath"])
```
