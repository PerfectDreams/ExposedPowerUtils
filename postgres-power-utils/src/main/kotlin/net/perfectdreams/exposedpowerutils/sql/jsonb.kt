package net.perfectdreams.exposedpowerutils.sql

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject
import java.sql.ResultSet

class JsonBinary : ColumnType() {
    override fun sqlType() = "JSONB"

    override fun readObject(rs: ResultSet, index: Int): Any? {
        return rs.getString(index)
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val obj = PGobject()
        obj.type = "jsonb"
        obj.value = value as String?
        stmt[index] = obj
    }
}

fun Table.jsonb(name: String): Column<String> = registerColumn(name, JsonBinary())