package io.fls.blogapp.core.model

import java.time.ZonedDateTime

data class UserThread(
    var id: String?,
    var name: String,
    val content: String,
    val userId: String,
    val userName: String,
    val creationDate: ZonedDateTime
)