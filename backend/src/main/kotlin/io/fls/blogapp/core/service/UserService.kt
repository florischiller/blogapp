package io.fls.blogapp.core.service

import io.fls.blogapp.core.exceptions.ForbiddenException
import io.fls.blogapp.core.exceptions.KonfliktException
import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.ports.UserPersistencePort
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface UserService {
    fun verify(name: String, password: String): User?
    fun findByName(name: String): User?
    fun save(user: User): User
}

class UserServiceImpl(
    private val userPersistencePort: UserPersistencePort
) : UserService {
    override fun findByName(name: String): User? {
        return userPersistencePort.findByName(name)
    }

    override fun verify(name: String, password: String): User? {
        val user = userPersistencePort.findByName(name)
        if (user == null) return null
        else if (hashPassword(password) == user.password) return user
        else throw ForbiddenException("Die Anfrage wurde aus ermangelung von Rechten nicht ausgeführt")
    }

    override fun save(user: User): User {
        if (userPersistencePort.findByName(user.username) != null) {
            throw KonfliktException("username: A User with this name already exists")
        }
        return userPersistencePort.save(
            user.copy(password = hashPassword(user.password))
        )
    }
}

fun hashPassword(password: String): String {
    val hashKey = hex("6819b57a326945c1968f45236589")
    val algorithm = "HmacSHA512"
    val hmacKey = SecretKeySpec(hashKey, algorithm)

    val hmac = Mac.getInstance(algorithm)
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}