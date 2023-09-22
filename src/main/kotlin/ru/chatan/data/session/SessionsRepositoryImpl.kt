package ru.chatan.data.session

import ru.chatan.data.dao.SessionStorage
import ru.chatan.data.dao.SessionsRepository
import ru.chatan.data.session.models.Session

class SessionsRepositoryImpl(
    private val sessionStorage: SessionStorage = IMSStorage.newInstance()
) : SessionsRepository {
    override fun save(session: Session) {
        sessionStorage.save(session = session)
    }

    override fun get(userId: Long, chatId: Long): List<Session> =
        sessionStorage.get(userId = userId, chatId = chatId)

    override fun get(chatId: Long): List<Session> =
        sessionStorage.get(chatId = chatId)

    override fun remove(userId: Long, chatId: Long) {
        sessionStorage.remove(userId = userId, chatId = chatId)
    }

    companion object {
        private var INSTANCE: SessionsRepository? = null

        fun newInstance(): SessionsRepository {
            val instance = INSTANCE
            if (instance != null) return instance

            val temp = SessionsRepositoryImpl()
            INSTANCE = temp
            return temp
        }
    }
}