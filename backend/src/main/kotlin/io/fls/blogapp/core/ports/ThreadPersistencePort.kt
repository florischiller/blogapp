package io.fls.blogapp.core.ports

import io.fls.blogapp.core.model.UserThread

interface ThreadPersistencePort {
    fun create(thread: UserThread): UserThread
    fun findById(id: String): UserThread?
    fun delete(id: String)
}