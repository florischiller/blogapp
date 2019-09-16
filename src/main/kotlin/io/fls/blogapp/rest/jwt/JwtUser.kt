package io.fls.blogapp.rest.jwt

import io.ktor.auth.Principal

data class JwtUser(
    val id: String,
    val name: String,
    val email: String
) : Principal