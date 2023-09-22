package ru.chatan.data.dao

import ru.chatan.data.session.models.Session

interface SessionStorage {

    fun save(session: Session)
    fun get(userId: Long, chatId: Long): List<Session>
    fun get(chatId: Long): List<Session>
    fun remove(userId: Long, chatId: Long)

    fun getAll(): List<Session>

}