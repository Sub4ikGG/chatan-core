package ru.chatan.data.session

import ru.chatan.data.dao.SessionStorage
import ru.chatan.data.session.models.Session

class IMSStorage : SessionStorage {

    private val sessions = mutableListOf<Session>()

    override fun save(session: Session) {
        sessions.add(session)
    }

    override fun get(userId: Long, chatId: Long): List<Session> =
        sessions.filter { it.userId == userId && it.chatId == chatId }

    override fun get(chatId: Long): List<Session> =
        sessions.filter { it.chatId == chatId }

    override fun remove(userId: Long, chatId: Long) {
        sessions.removeIf { it.userId == userId && it.chatId == chatId }
    }

    override fun getAll() = sessions

    companion object {
        private var INSTANCE: SessionStorage? = null

        fun newInstance(): SessionStorage {
            if (INSTANCE != null) INSTANCE!!

            val temp = IMSStorage()
            INSTANCE = temp
            return temp
        }
    }

}