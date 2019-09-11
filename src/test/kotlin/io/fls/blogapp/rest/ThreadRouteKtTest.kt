package io.fls.blogapp.rest

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThreadRouteKtTest {
    @Test
    fun testPostThread() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/api/v1/threads").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}