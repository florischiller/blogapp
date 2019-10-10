package io.fls.blogapp.persistence.entities

import org.bson.codecs.pojo.annotations.BsonId

data class UserDbo(
    @BsonId
    val _id: String?,
    val name: String,
    val password: String,
    val email: String
)