package ru.chatan.data.database.user

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.dbQuery
import ru.chatan.data.database.user.models.UserModel

class UserService(private val database: Database) {

    object User : Table() {
        val id = long("id").autoIncrement()
        val name = varchar("name", 64)
        val password = varchar("password", 256)

        override val primaryKey: PrimaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(User)
        }
    }

    suspend fun create(name: String, password: String): Long {
        return dbQuery {
            User.insert {
                it[User.name] = name
                it[User.password] = password
            }[User.id]
        }
    }

    suspend fun fetch(name: String): UserModel? {
        return dbQuery {
            val row = User.select(where = { User.name eq name }).singleOrNull() ?: return@dbQuery null

            UserModel(
                id = row[User.id],
                name = row[User.name],
                password = row[User.password]
            )
        }
    }

    suspend fun fetch(userId: Long): UserModel? {
        return dbQuery {
            val row = User.select(where = { User.id eq userId }).singleOrNull() ?: return@dbQuery null

            UserModel(
                id = row[User.id],
                name = row[User.name],
                password = row[User.password]
            )
        }
    }

    suspend fun update(userId: Long, name: String) {
        dbQuery {
            User.update(where = { User.id eq userId }) {
                it[User.name] = name
            }
        }
    }

}