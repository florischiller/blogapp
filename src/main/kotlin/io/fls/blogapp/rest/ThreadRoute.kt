package io.fls.blogapp.rest

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.routeThreads() {
    post("/threads") {
        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }
}