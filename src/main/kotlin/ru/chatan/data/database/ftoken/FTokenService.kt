package ru.chatan.data.database.ftoken

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class FTokenService(private val database: Database) {

    object FToken: Table() {
        val id = long("id").autoIncrement()
        val userId = long("id")
        val deviceId = varchar("device_id", 256)
        val token = varchar("token", 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(FToken)
        }
    }

}