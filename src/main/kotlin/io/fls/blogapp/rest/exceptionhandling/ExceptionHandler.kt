package io.fls.blogapp.rest.exceptionhandling

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

data class FehlerDto(val fehler: String, val status: Int)

fun StatusPages.Configuration.exceptionHandler() {
    exception<MissingKotlinParameterException> {
        call.respond(
            message = FehlerDto("Ein Pflichtwert war leer!", HttpStatusCode.BadRequest.value),
            status = HttpStatusCode.BadRequest
        )
    }
    exception<BadRequestException> { cause ->
        call.respond(
            message = FehlerDto(cause.localizedMessage, HttpStatusCode.BadRequest.value),
            status = HttpStatusCode.BadRequest
        )
    }
    exception<JsonParseException> { cause ->
        call.respond(
            message = FehlerDto(
                "Es wurde ein invalider JSON-String übergeben",
                HttpStatusCode.BadRequest.value
            ),
            status = HttpStatusCode.BadRequest
        )
    }
    exception<NotFoundException> { cause ->
        call.respond(
            message = FehlerDto(cause.localizedMessage, HttpStatusCode.NotFound.value),
            status = HttpStatusCode.NotFound
        )
    }
}