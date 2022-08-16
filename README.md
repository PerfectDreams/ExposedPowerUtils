<h1 align="center">✨ ExposedPowerUtils ✨</h1>

Utilities and Extensions for [Exposed](https://github.com/JetBrains/Exposed), because while Exposed is a pretty nice framework, it tries to support a lot of SQL dialects, so a lot of advanced features that aren't supported by the SQL standard aren't supported in Exposed.

Thankfully Exposed is very extendable, allowing you to support features that it doesn't support out of the box! This repository contains a bunch of Exposed tidbits that I use on a lot of my projects.

## Getting Started
```kotlin
repositories {
  mavenCentral()
  maven("https://repo.perfectdreams.net/")
}
```

Then, in your dependencies...
```kotlin
dependencies {
  implementation("net.perfectdreams.exposedpowerutils:ModuleNameHere:CurrentVersionHere")
}
```

Here's an example!
```kotlin
dependencies {
  implementation("net.perfectdreams.exposedpowerutils:postgres-java-time:1.1.0")
}
```

## Modules
* [Exposed Power User Utils](/exposed-power-utils)
* [PostgreSQL Timestamp Support (Java Time)](/postgres-java-time)
* [PostgreSQL Power User Utils](/postgres-power-utils)

---

**Similar projects:**
* https://github.com/Benjozork/exposed-postgres-extensions
* https://github.com/LukasForst/exposed-upsert