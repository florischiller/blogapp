package io.fls.blogapp.rest.routes

import io.fls.blogapp.core.model.UserThread
import io.fls.blogapp.core.service.ThreadService
import io.fls.blogapp.rest.jwt.JwtUser
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.time.ZonedDateTime

data class CreateThreadDto(val name: String, val content: String)
data class ThreadResponseDto(
    val name: String,
    val id: String?,
    val content: String,
    val userId: String,
    val userName: String,
    val creationDate: ZonedDateTime
)

fun Route.routeThreads() {
    val threadService: ThreadService by inject()
    route("/threads") {
        get("/") {
            val limit: Int = call.request.queryParameters["limit"]?.toInt() ?: 20
            val offset: Int = call.request.queryParameters["offset"]?.toInt() ?: 0
            val foundThreads = threadService.findAll(limit, offset)
                ?: throw NotFoundException(message = "Der Eintrag wurde nicht gefunden")
            val mappedThreads = foundThreads.map { transformToResponse(it) }
            call.respond(message = mappedThreads, status = HttpStatusCode.OK)
        }
        authenticate {
            post {
                val thread = call.receive<CreateThreadDto>()
                val user: JwtUser = call.authentication.principal as JwtUser
                val createdThread = threadService.create(transformToModell(thread, user))
                call.response.header("location", call.request.path() + "/" + createdThread.id)
                call.respond(message = transformToResponse(createdThread), status = HttpStatusCode.Created)
            }
            get("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException(message = "Die übergebene ID war leer")
                val foundThread =
                    threadService.findById(id) ?: throw NotFoundException(message = "Der Eintrag wurde nicht gefunden")
                call.respond(message = transformToResponse(foundThread), status = HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException(message = "Die übergebene ID war leer")
                threadService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }

        }
    }
}

private fun transformToModell(thread: CreateThreadDto, user: JwtUser): UserThread {
    return UserThread(
        name = thread.name, id = null, content = thread.content, userId = user.id, userName = user.name,
        creationDate = ZonedDateTime.now()
    )
}

private fun transformToResponse(createdThread: UserThread) =
    ThreadResponseDto(
        name = createdThread.name,
        id = createdThread.id,
        content = createdThread.content,
        userId = createdThread.userId,
        userName = createdThread.userName,
        creationDate = createdThread.creationDate
    )