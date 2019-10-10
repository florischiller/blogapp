package io.fls.blogapp.core.model

data class User(
    val id: String?,
    val name: String,
    val password: String,
    val email: String
)