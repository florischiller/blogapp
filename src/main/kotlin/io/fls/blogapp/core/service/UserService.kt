package io.fls.blogapp.core.service

import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.ports.UserPersistencePort
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface UserService {
    fun verify(id: String, password: String): User?
    fun findById(id: String): User?
    fun save(user: User): User
}

class UserServiceImpl(
    private val userPersistencePort: UserPersistencePort
) : UserService {
    override fun findById(id: String): User? {
        return userPersistencePort.findById(id)
    }

    override fun verify(id: String, password: String): User? {
        val user = userPersistencePort.findById(id)
        if (hashPassword(password) == user?.password) return user
        else return null
    }

    override fun save(user: User): User {
        return userPersistencePort.save(
            user.copy(password = hashPassword(user.password))
        )
    }
}

fun hashPassword(password: String): String {
    val hashKey = hex("6819b57a326945c1968f45236589")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}