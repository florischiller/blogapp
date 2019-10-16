package io.fls.blogapp.core.ports

import io.fls.blogapp.core.model.UserThread
import java.util.stream.Stream

interface ThreadPersistencePort {
    fun create(thread: UserThread): UserThread
    fun findById(id: String): UserThread?
    fun findAll(limit: Int, offset: Int): Stream<UserThread>?
    fun delete(id: String)
}