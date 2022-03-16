<h1 align="center">✨ ExposedPowerUtils ✨</h1>

Utilities and Extensions for [Exposed](https://github.com/JetBrains/Exposed), because while Exposed is a pretty nice framework, it tries to support a lot of SQL dialects, so a lot of advanced features that aren't supported by the SQL standard aren't supported in Exposed.

Thankfully Exposed is very extendable, allowing you to support features that it doesn't support out of the box! This repository contains a bunch of Exposed tidbits that I use on a lot of my projects.

## Modules
### `:exposed-power-utils`
Contains general purpose extensions and utilities for Exposed, not bound to a specific SQL dialect.

### `:postgres-power-utils`
Contains PostgreSQL extensions and utilities for Exposed.

* Basic `jsonb` support
  * You get and store data using `String`s, the way how you are going to serialize and deserialize your data is up to you!
* Create and update PostgreSQL enums with Java enums

**Example:** TODO

### `:postgres-java-time`

`Instant` `timestamp(...)` with PostgreSQL's `TIMESTAMP WITH TIMEZONE`.

* Avoids the issue described in [#1536](https://github.com/JetBrains/Exposed/issues/1356), [#1305](https://github.com/JetBrains/Exposed/issues/1305), [#886](https://github.com/JetBrains/Exposed/issues/886)

**Example:** TODO

---
**Similar projects:**
* https://github.com/Benjozork/exposed-postgres-extensions