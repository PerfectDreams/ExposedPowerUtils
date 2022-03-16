import net.perfectdreams.exposedpowerutils.sql.createOrUpdatePostgreSQLEnum
import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.jsonb
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Rule
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Instant
import java.util.*

class TestStuff {
    @Rule
    @JvmField
    val postgres = PostgreSQLContainer("postgres:14")

    @Test
    fun `test postgresql extensions`() {
        val test = Database.connect(postgres.jdbcUrl, user = postgres.username, password = postgres.password)

        transaction(test) {
            // You need to create the PostgreSQL enum before creating any tables that depend on the enum
            createOrUpdatePostgreSQLEnum(CharacterType.values())

            SchemaUtils.createMissingTablesAndColumns(PlayerInfo)

            PlayerInfo.insert {
                // The jsonb column returns a String, the serialization and deserialization is up to you!
                it[PlayerInfo.information] = "{\"name\":\"MrPowerGamerBR\"}"
                it[PlayerInfo.favoriteCharacter] = CharacterType.LORITTA
            }
        }
    }

    object PlayerInfo : LongIdTable() {
        val information = jsonb("information")
        val favoriteCharacter = postgresEnumeration<CharacterType>("favorite_character")
    }

    // Keep in mind that there are some reserved types in PostgreSQL
    // https://www.postgresql.org/docs/current/sql-keywords-appendix.html
    enum class CharacterType {
        LORITTA,
        PANTUFA,
        GABRIELA
    }
}