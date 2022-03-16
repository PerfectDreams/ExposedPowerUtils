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
  implementation("net.perfectdreams.exposedpowerutils:postgres-java-time:1.0.0")
}
```

## Modules
### `:exposed-power-utils`
Contains general purpose extensions and utilities for Exposed, not bound to a specific SQL dialect.

### `:postgres-power-utils`
Contains PostgreSQL extensions and utilities for Exposed.

* Basic `jsonb` support
  * You get and store data using `String`s, the way how you are going to serialize and deserialize your data is up to you!
* Create and update PostgreSQL enums with Java enums

**Example:** https://github.com/PerfectDreams/ExposedPowerUtils/blob/80fa05d419ae468b7901bcf0567e3365906b384d/tests/src/test/kotlin/TestStuff.kt#L28

### `:postgres-java-time`

`Instant` `timestamp(...)` with PostgreSQL's `TIMESTAMP WITH TIMEZONE`.

* Avoids the issue described in [#1536](https://github.com/JetBrains/Exposed/issues/1356), [#1305](https://github.com/JetBrains/Exposed/issues/1305), [#886](https://github.com/JetBrains/Exposed/issues/886)
* Here's why you should use `TIMESTAMP WITH TIMEZONE`: https://www.toolbox.com/tech/data-management/blogs/zone-of-misunderstanding-092811/

**Example:** https://github.com/PerfectDreams/ExposedPowerUtils/blob/80fa05d419ae468b7901bcf0567e3365906b384d/tests/src/test/kotlin/PostgresJavaTimeTest.kt#L26

---

**Similar projects:**
* https://github.com/Benjozork/exposed-postgres-extensions
* https://github.com/LukasForst/exposed-upsert