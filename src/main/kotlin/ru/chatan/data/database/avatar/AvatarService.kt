package ru.chatan.data.database.avatar

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.chatan.data.database.Service
import ru.chatan.data.database.dbQuery

class AvatarService(override val database: Database) : Service() {

    object Avatar : Table() {
        val id = long("id").autoIncrement()
        val userId = long("user_id")
        val uuid = varchar("uuid", 512)

        override val primaryKey: PrimaryKey
            get() = PrimaryKey(id)
    }

    suspend fun create(userId: Long, uuid: String) {
        dbQuery {
            Avatar.insert {
                it[Avatar.userId] = userId
                it[Avatar.uuid] = uuid
            }
        }
    }

    suspend fun fetch(userId: Long): AvatarDTO? {
        return dbQuery {
            val row = Avatar.select(where = { Avatar.userId eq userId }).singleOrNull() ?: return@dbQuery null

            AvatarDTO(
                id = row[Avatar.id],
                userId = row[Avatar.userId],
                uuid = row[Avatar.uuid]
            )
        }
    }

}