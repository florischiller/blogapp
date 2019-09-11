package io.fls.blogapp.rest

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.route
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {}
    }
    install(CallLogging) {}

    routing {
        defaultRoutes()
        route("/api/v1") {
            routeThreads()
        }
    }
}
