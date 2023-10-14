package ru.chatan.data.dao

import ru.chatan.data.session.models.Session

interface SessionsRepository {

    /**
     * Saving user session
     */
    fun save(session: Session)

    /**
     * Gets user session list
     */
    fun get(userId: Long, chatId: Long): List<Session>

    /**
     * Gets all chat sessions
     */
    fun get(chatId: Long): List<Session>

    /**
     * Remove session
     */
    fun remove(userId: Long, chatId: Long)

}