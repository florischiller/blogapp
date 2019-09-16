package io.fls.blogapp.rest.routes

import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.service.UserService
import io.fls.blogapp.rest.jwt.JwtUser
import io.fls.blogapp.rest.jwt.JwtVerifer
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.ktor.ext.inject

data class UserRequestDto(val password: String)
data class UserCreateDto(
    val name: String,
    val password: String,
    val email: String
)

data class UserJwtResponseDto(
    val jwt: String
)

data class UserCreatedResponseDto(
    val id: String?
)

fun Route.routeUser() {
    val userService: UserService by inject()
    route("/users") {
        post("/{id}/jwt") {
            val id = call.parameters["id"] ?: throw BadRequestException(message = "Die übergebene ID war leer")
            val userRequest = call.receive<UserRequestDto>()
            val user = userService.verify(id, userRequest.password)
                ?: throw NotFoundException(message = "Der Eintrag wurde nicht gefunden")
            val jwt = JwtVerifer.makeToken(transformToJwtUser(user))
            call.respond(message = UserJwtResponseDto(jwt), status = HttpStatusCode.OK)
        }
        post {
            val userRequest = call.receive<UserCreateDto>()
            val user = userService.save(transformToModel(userRequest))
            call.respond(message = UserCreatedResponseDto(id = user.id), status = HttpStatusCode.Created)
        }
    }
}

private fun transformToJwtUser(user: User): JwtUser {
    return JwtUser(name = user.name, id = user.id ?: "", email = user.email)
}

private fun transformToModel(user: UserCreateDto): User =
    User(name = user.name, email = user.email, password = user.password, id = null)
