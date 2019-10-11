package io.fls.blogapp.core.ports

import io.fls.blogapp.core.model.User

interface UserPersistencePort {
    fun findByName(name: String): User?
    fun save(user: User): User
}