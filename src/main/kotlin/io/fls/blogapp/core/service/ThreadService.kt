package io.fls.blogapp.core.service

import io.fls.blogapp.core.model.UserThread
import io.fls.blogapp.core.ports.ThreadPersistencePort

interface ThreadService {
    fun findById(id: String): UserThread?
    fun delete(id: String)
    fun create(thread: UserThread): UserThread
}

class ThreadServiceImpl(
    private val threadPersistencePort: ThreadPersistencePort
) : ThreadService {

    override fun findById(id: String): UserThread? {
        return threadPersistencePort.findById(id)
    }

    override fun create(thread: UserThread): UserThread {
        return threadPersistencePort.create(thread)
    }

    override fun delete(id: String) {
        val userThread = threadPersistencePort.findById(id)
        if (userThread != null) {
            threadPersistencePort.delete(id)
        }
    }
}