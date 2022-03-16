package net.perfectdreams.exposedpowerutils.sql

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.postgresql.util.PGobject

/**
 * Creates and updates a PostgreSQL enum based on a Kotlin enum
 *
 * **This does not remove values from the PostgreSQL enum, and it does not insert new enums based on order!**
 *
 * @param enumValues a callback that provides a list of the valid enum values
 */
inline fun <reified T : Enum<T>> Transaction.createOrUpdatePostgreSQLEnum(enumValues: Array<T>) {
    val valueNames = enumValues.map { it.name }
    val clazzName = T::class.simpleName!!
    val psqlType = clazzName.lowercase()
    val joined = valueNames.joinToString { "'$it'" }

    val alreadyInsertedEnumValues = mutableSetOf<String>()

    var enumExists = false
    exec("SELECT 1 FROM pg_type WHERE typname = '$psqlType';") {
        // If there's any row, then it means that the type exists
        enumExists = it.next()
    }

    // https://stackoverflow.com/a/30417601/7271796
    if (enumExists) {
        exec("SELECT unnest(enum_range(null, null::$psqlType)) AS enum_value;") {
            while (it.next())
                alreadyInsertedEnumValues.add(it.getString("enum_value"))
        }
    }

    val missingEnums = valueNames.filter { it !in alreadyInsertedEnumValues }

    if (alreadyInsertedEnumValues.isEmpty()) {
        exec("CREATE TYPE ${clazzName.lowercase()} AS ENUM ($joined);")
    } else if (missingEnums.isNotEmpty()) {
        for (missingEnum in missingEnums) {
            exec("ALTER TYPE ${clazzName.lowercase()} ADD VALUE '$missingEnum';")
        }
    }
}

inline fun <reified T : Enum<T>> Table.postgresEnumeration(
    columnName: String
) = customEnumeration(columnName, T::class.simpleName!!.lowercase(),
    { value -> enumValueOf<T>(value as String) }, { PGEnum(T::class.simpleName!!.lowercase(), it) })

// From https://github.com/JetBrains/Exposed/wiki/DataTypes
class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}