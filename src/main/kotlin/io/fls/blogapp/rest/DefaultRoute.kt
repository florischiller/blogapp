package io.fls.blogapp.rest

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get

internal fun Routing.defaultRoutes() {
    routeFilesystem()
    routeWelcome()
}

fun Route.routeFilesystem() {
    static {
        resources("static")
    }
}

fun Route.routeWelcome() {
    get("/") {
        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }
}