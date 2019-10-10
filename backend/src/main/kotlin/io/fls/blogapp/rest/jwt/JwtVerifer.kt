package io.fls.blogapp.rest.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtVerifer {

    private val config = JwtConfig()
    private val algorithm = Algorithm.HMAC512(config.secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(config.issuer)
        .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(user: JwtUser): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(config.issuer)
        .withClaim("id", user.id)
        .withClaim("name", user.name)
        .withClaim("email", user.email)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    fun getRealm(): String = config.issuer

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + config.validityInMs)
}