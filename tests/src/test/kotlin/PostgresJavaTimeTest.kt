import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Rule
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Instant
import java.util.*

class PostgresJavaTimeTest {
    @Rule
    @JvmField
    val postgres = PostgreSQLContainer("postgres:14")

    @Test
    fun `test timestamp with time zone`() {
        val test = Database.connect(postgres.jdbcUrl, user = postgres.username, password = postgres.password)

        transaction(test) {
            SchemaUtils.createMissingTablesAndColumns(ActionLog)

            // Exposed's "timestamp" implementation has an issue where it depends on the system's time zone.
            // So if you inserted "Instant.now()", it would depend on your system time zone, and that's not good!
            // Imagine if you have two machines in different time zones, and you are trying to keep an audit log,
            // even if both codes were executed at the same time, due to time zone differences, both times would
            // be different!
            //
            // If you ran the following code with Exposed's "timestamp", it will fail.
            //
            // That's why we use the "timestampWithTimeZone", read more at https://www.toolbox.com/tech/data-management/blogs/zone-of-misunderstanding-092811/
            val now = Instant.now()

            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            val id1 = ActionLog.insertAndGetId {
                it[ActionLog.timestamp] = now
                it[ActionLog.text] = "Hello from UTC!"
            }

            TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"))
            val id2 = ActionLog.insertAndGetId {
                it[ActionLog.timestamp] = now
                it[ActionLog.text] = "Hello from America/Sao_Paulo!"
            }

            ActionLogDAO.new {
                this.timestamp = now
                this.text = "Hello from America/Sao_Paulo! (DAO)"
            }

            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

            val timezone1 = ActionLog.select { ActionLog.id eq id1 }.first()[ActionLog.timestamp]
            val timezone2 = ActionLog.select { ActionLog.id eq id2 }.first()[ActionLog.timestamp]
            assert(timezone1 == timezone2) { "The instants aren't equal! $timezone1 $timezone2" }
        }
    }

    object ActionLog : LongIdTable() {
        val timestamp = timestampWithTimeZone("timestamp")
        val text = text("text")
    }

    class ActionLogDAO(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<ActionLogDAO>(ActionLog)

        var text by ActionLog.text
        var timestamp by ActionLog.timestamp
    }
}