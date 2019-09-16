package io.fls.blogapp.persistence.entities

import org.bson.codecs.pojo.annotations.BsonId
import java.time.ZonedDateTime

data class UserThreadDbo(
    @BsonId val _id: String?,
    val name: String,
    val content: String,
    val userId: String,
    val userName: String,
    val creationDate: ZonedDateTime
)