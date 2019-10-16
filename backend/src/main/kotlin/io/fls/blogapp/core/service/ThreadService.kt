package io.fls.blogapp.core.service

import io.fls.blogapp.core.model.UserThread
import io.fls.blogapp.core.ports.ThreadPersistencePort
import java.util.stream.Stream

interface ThreadService {
    fun findAll(limit: Int, offset: Int): Stream<UserThread>?
    fun findById(id: String): UserThread?
    fun delete(id: String)
    fun create(thread: UserThread): UserThread
}

class ThreadServiceImpl(
    private val threadPersistencePort: ThreadPersistencePort
) : ThreadService {
    override fun findAll(limit: Int, offset: Int): Stream<UserThread>? {
        return threadPersistencePort.findAll(limit, offset)
    }

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