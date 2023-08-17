package ru.chatan.data.database.user

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class UserAvatarService(private val database: Database) {

    object UserAvatar: Table() {
        val id = long("id").autoIncrement()
        val userId = long("user_id")
        val uuid = varchar("uuid", 256)

        override val primaryKey: PrimaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UserAvatar)
        }
    }

}