package io.fls.blogapp.rest.routes

import io.ktor.application.call
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get

internal fun Routing.defaultRoutes() {
    routeFilesystem()
}

fun Route.routeFilesystem() {
    get("/") {
        call.respondRedirect("/index.html")
    }
    static {
        resources("static")
    }
}