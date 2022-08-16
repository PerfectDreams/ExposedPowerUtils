# Exposed PostgreSQL Timestamp Support (Java Time)

`Instant` `timestamp(...)` with PostgreSQL's `TIMESTAMP WITH TIMEZONE`.

* Avoids the issue described in [#1536](https://github.com/JetBrains/Exposed/issues/1356), [#1305](https://github.com/JetBrains/Exposed/issues/1305), [#886](https://github.com/JetBrains/Exposed/issues/886)
* Here's why you should use `TIMESTAMP WITH TIMEZONE`: https://www.toolbox.com/tech/data-management/blogs/zone-of-misunderstanding-092811/

**Example:** https://github.com/PerfectDreams/ExposedPowerUtils/blob/80fa05d419ae468b7901bcf0567e3365906b384d/tests/src/test/kotlin/PostgresJavaTimeTest.kt#L23-L60