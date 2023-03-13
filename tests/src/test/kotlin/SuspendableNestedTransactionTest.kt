import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import kotlinx.coroutines.*
import net.perfectdreams.exposedpowerutils.sql.transaction
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
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

    private suspend fun differentFunctionCall(t1: Transaction, db: Database) = transaction(Dispatchers.IO, db) {
        assert(t1 == this) { "Different Function Call transaction is not equal to Transaction1!" }
    }
}