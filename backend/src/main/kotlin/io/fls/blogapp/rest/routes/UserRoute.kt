package io.fls.blogapp.rest.routes

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.service.UserService
import io.fls.blogapp.rest.jwt.JwtUser
import io.fls.blogapp.rest.jwt.JwtVerifer
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.ktor.ext.inject

data class UserRequestDto(val password: String)

@JsonIgnoreProperties("confirmPassword")
data class UserCreateDto(
    val username: String,
    val password: String,
    val email: String
)

data class UserJwtResponseDto(
    val jwt: String
)

data class UserDto(
    val id: String?,
    val username: String,
    val email: String
)

fun Route.routeUser() {
    val userService: UserService by inject()
    route("/users") {
        post("/{name}/jwt") {
            val name = call.parameters["name"] ?: throw BadRequestException(message = "Die übergebene ID war leer")
            val userRequest = call.receive<UserRequestDto>()
            val user = userService.verify(name, userRequest.password)
                ?: throw NotFoundException(message = "Der Eintrag wurde nicht gefunden")
            val jwt = JwtVerifer.makeToken(transformToJwtUser(user))
            call.respond(message = UserJwtResponseDto(jwt), status = HttpStatusCode.OK)
        }
        post {
            val userRequest = call.receive<UserCreateDto>()
            val user = userService.save(transformToModel(userRequest))
            call.response.header("location", call.request.path() + "/" + user.username)
            call.respond(message = transformToDto(user), status = HttpStatusCode.Created)
        }
        get("/{name}") {
            val name =
                call.parameters["name"] ?: throw BadRequestException(message = "Ein Pflichtwert war leer!")
            val user = userService.findByName(name)
                ?: throw NotFoundException(message = "Der übergeben Nutzername war leer")
            call.respond(message = transformToDto(user), status = HttpStatusCode.OK)
        }
    }
}

private fun transformToJwtUser(user: User): JwtUser {
    return JwtUser(name = user.username, id = user.id ?: "", email = user.email)
}

private fun transformToModel(user: UserCreateDto): User =
    User(username = user.username, email = user.email, password = user.password, id = null)

private fun transformToDto(user: User): UserDto =
    UserDto(username = user.username, email = user.email, id = user.id)
