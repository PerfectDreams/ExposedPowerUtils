import net.perfectdreams.exposedpowerutils.sql.createOrUpdatePostgreSQLEnum
import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.jsonb
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.exposedpowerutils.sql.upsert
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Rule
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Instant
import java.util.*

class UpsertTest {
    @Rule
    @JvmField
    val postgres = PostgreSQLContainer("postgres:14")

    @Test
    fun `test upsert`() {
        val test = Database.connect(postgres.jdbcUrl, user = postgres.username, password = postgres.password)

        transaction(test) {
            SchemaUtils.createMissingTablesAndColumns(Counter)

            Counter.upsert(Counter.id) {
                it[Counter.id] = 1
                it[Counter.counter] = 0
            }

            Counter.upsert(Counter.id) {
                it[Counter.id] = 1
                it[Counter.counter] = 4002
            }

            val counterResult = Counter.select { Counter.id eq 1 }.first()
            val r = counterResult[Counter.counter]

            assert(r == 4002) { "Counter result is not 4002!" }
        }
    }

    object Counter : LongIdTable() {
        val counter = integer("counter")
    }
}