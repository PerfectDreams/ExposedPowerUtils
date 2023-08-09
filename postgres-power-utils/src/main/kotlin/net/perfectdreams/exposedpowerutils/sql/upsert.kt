package net.perfectdreams.exposedpowerutils.sql

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

// Based off https://github.com/JetBrains/Exposed/issues/167#issuecomment-952734972
/**
 * Inserts or updates a single item based on conflicting [keys].
 *
 * ```kotlin
 * val item = ...
 * MyTable.upsert(MyTable.id) {
 *      it[id] = item.id
 *      it[value1] = item.value1
 * }
 * ```
 */
fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit
) =
    InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }

class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction, prepared)} $onConflict"
    }
}

/**
 * Batch inserts or updates items based on conflicting [keys].
 *
 * ```kotlin
 * val items = listOf(...)
 * MyTable.batchUpsert(MyTable.id, items) { it, item ->
 *      it[id] = item.id
 *      it[value1] = item.value1
 * }
 * ```
 *
 * **Attention:** While Exposed's [upsert] [shouldReturnGeneratedValues] is set to true by default, [batchUpsert]'s [shouldReturnGeneratedValues] is set to false!
 *
 * Setting [shouldReturnGeneratedValues] to false speeds up batch insert/upserts performance when paired with the reWriteBatchedInserts PostgreSQL driver option, because it allows the
 * JDBC driver to batch the queries into single statements.
 */
fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    ignore: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
    body: T.(BatchInsertStatement, E) -> Unit
) =
    BatchInsertOrUpdate(this, keys = keys, isIgnore = ignore, shouldReturnGeneratedValues = shouldReturnGeneratedValues).apply {
        data.forEach {
            addBatch()
            body(this, it)
        }
        execute(TransactionManager.current())
    }

class BatchInsertOrUpdate(
    table: Table,
    isIgnore: Boolean,
    private vararg val keys: Column<*>,
    shouldReturnGeneratedValues: Boolean
) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues) {
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction, prepared)} $onConflict"
    }
}