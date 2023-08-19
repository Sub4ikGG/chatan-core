package ru.chatan.data.database.user

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.dbQuery
import ru.chatan.data.database.user.models.TokenModel

class UserTokenService(private val database: Database) {

    object UserToken: Table() {
        val id = long("id").autoIncrement()
        val userId = long("user_id")
        val deviceId = varchar("device_id", 256)
        val refreshToken = varchar("refresh_token", 256)

        override val primaryKey: PrimaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UserToken)
        }
    }

    suspend fun create(userId: Long, deviceId: String, refreshToken: String) {
        dbQuery {
            UserToken.insert {
                it[UserToken.userId] = userId
                it[UserToken.deviceId] = deviceId
                it[UserToken.refreshToken] = refreshToken
            }
        }
    }

    suspend fun fetch(deviceId: String, refreshToken: String): TokenModel? {
        return dbQuery {
            val row = UserToken.select(where = { (UserToken.deviceId eq deviceId) and (UserToken.refreshToken eq refreshToken) }).singleOrNull()
                ?: return@dbQuery null

            TokenModel(
                id = row[UserToken.id],
                userId = row[UserToken.userId],
                deviceId = row[UserToken.deviceId],
                refreshToken = row[UserToken.refreshToken]
            )
        }
    }

    suspend fun fetch(deviceId: String, userId: Long): TokenModel? {
        return dbQuery {
            val row = UserToken.select(where = { (UserToken.deviceId eq deviceId) and (UserToken.userId eq userId) }).singleOrNull()
                ?: return@dbQuery null

            TokenModel(
                id = row[UserToken.id],
                userId = row[UserToken.userId],
                deviceId = row[UserToken.deviceId],
                refreshToken = row[UserToken.refreshToken]
            )
        }
    }

    suspend fun update(userId: Long, deviceId: String, refreshToken: String) {
        dbQuery {
            UserToken.update(where = { (UserToken.userId eq userId) and (UserToken.deviceId eq deviceId) }) {
                it[UserToken.refreshToken] = refreshToken
            }
        }
    }

}