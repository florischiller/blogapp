package io.fls.blogapp.rest.routes

import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.Route
import io.ktor.routing.Routing

internal fun Routing.defaultRoutes() {
    routeFilesystem()
}

fun Route.routeFilesystem() {
    val pathToIndex = "static/index.html"

    static {
        resources("static")
    }
    resource("/", pathToIndex)
    resource("/threads", pathToIndex)
    resource("/user/login", pathToIndex)
    resource("/user/create", pathToIndex)
}