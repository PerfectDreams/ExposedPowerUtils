import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import kotlinx.coroutines.*
import net.perfectdreams.exposedpowerutils.sql.jsonb
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.exposedpowerutils.sql.transaction
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.select
import org.junit.Rule
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer

class SuspendableNestedTransactionTest {
    @Rule
    @JvmField
    val postgres = PostgreSQLContainer("postgres:14")

    @Test
    fun `test suspendable nested transactions`() {
        val hikariConfig = HikariConfig()

        hikariConfig.driverClassName = "org.postgresql.Driver"
        hikariConfig.jdbcUrl = postgres.jdbcUrl
        hikariConfig.username = postgres.username
        hikariConfig.password = postgres.password

        val hikariDataSource = HikariDataSource(hikariConfig)

        val test = Database.connect(hikariDataSource)

        runBlocking {
            coroutineScope {
                repeat(100) {
                    launch {
                        transaction(Dispatchers.IO, test) {
                            val transaction1 = this

                            transaction(Dispatchers.IO, test) {
                                val transaction2 = this

                                transaction(Dispatchers.IO, test) {
                                    val transaction3 = this

                                    transaction(Dispatchers.IO, test) {
                                        val transaction4 = this

                                        transaction(Dispatchers.IO, test) {
                                            val transaction5 = this

                                            assert(transaction1 == transaction2) { "Transaction1 is not equal to Transaction2!" }
                                            assert(transaction2 == transaction3) { "Transaction2 is not equal to Transaction3!" }
                                            assert(transaction3 == transaction4) { "Transaction3 is not equal to Transaction4!" }
                                            assert(transaction4 == transaction5) { "Transaction4 is not equal to Transaction5!" }

                                            differentFunctionCall(transaction1, db)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test suspendable nested transactions repeat workaround`() {
        val hikariConfig = HikariConfig()

        hikariConfig.driverClassName = "org.postgresql.Driver"
        hikariConfig.jdbcUrl = postgres.jdbcUrl
        hikariConfig.username = postgres.username
        hikariConfig.password = postgres.password

        val hikariDataSource = HikariDataSource(hikariConfig)

        val test = Database.connect(hikariDataSource)

        val totalRepeats = 5
        var repeats = 0

        println("starting REALLL")
        runBlocking {
            coroutineScope {
                transaction(Dispatchers.IO, test) {
                    println("creating the MISSING TABLES AND COLUMNS")
                    SchemaUtils.createMissingTablesAndColumns(Names)
                }

                println("DOING THE REPEAT THINGY WOW")
                repeat(10) {
                    try {
                        transaction(Dispatchers.IO, test, repetitions = totalRepeats) {
                            println("EXECUTING IT!!!")
                            repeats++

                            // We want to intentionally throw an error here
                            Names.select { Names.name.regexp("\\Q") }
                                .toList()
                        }
                    } catch (e: ExposedSQLException) {
                        val message = e.message ?: error("Exception does not have an message!")
                        if (message.contains("Connection is closed")) {
                            error("Connection is closed when it shouldn't be closed")
                        }
                    }
                }
            }
        }
    }

    private suspend fun differentFunctionCall(t1: Transaction, db: Database) = transaction(Dispatchers.IO, db) {
        assert(t1 == this) { "Different Function Call transaction is not equal to Transaction1!" }
    }

    object Names : LongIdTable() {
        val name = text("name")
    }
}