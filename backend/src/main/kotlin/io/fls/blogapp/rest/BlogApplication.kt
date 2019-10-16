package io.fls.blogapp.rest

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.fls.blogapp.core.service.UserService
import io.fls.blogapp.modules.modules
import io.fls.blogapp.rest.exceptionhandling.exceptionHandler
import io.fls.blogapp.rest.jwt.JwtUser
import io.fls.blogapp.rest.jwt.JwtVerifer
import io.fls.blogapp.rest.routes.defaultRoutes
import io.fls.blogapp.rest.routes.routeThreads
import io.fls.blogapp.rest.routes.routeUser
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject
import java.text.SimpleDateFormat

fun main(args: Array<String>) {
    startKoin {
        modules(modules)
    }
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

fun Application.main() {
    val userService: UserService by inject()

    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            registerModule(Jdk8Module())
            dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        }
    }
    install(CallLogging) {}
    install(StatusPages) {
        exceptionHandler()
    }
    install(Authentication) {
        jwt {
            realm = JwtVerifer.getRealm()
            verifier(JwtVerifer.verifier)
            validate { credential -> validateUser(credential, userService) }
        }
    }

    routing {
        defaultRoutes()
        route("/api/v1") {
            routeUser()
            routeThreads()
        }
    }
}

fun validateUser(credential: JWTCredential, userService: UserService): Principal? {
    val name = credential.payload.getClaim("name").asString()
    return userService.findByName(name)?.let { user ->
        JwtUser(id = user.id ?: "", name = user.name, email = user.email)
    }
}
