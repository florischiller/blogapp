package io.fls.blogapp.rest

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationKtTest : KoinTest {

    @BeforeAll
    fun beforeAll() {
        startKoin {
            modules(io.fls.blogapp.modules.modules)
        }
    }

    @AfterAll
    fun autoClose() {
        stopKoin()
    }

    @Test
    fun testRootRedirect1() {
        withTestApplication({ main() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.Found, response.status())
                assertEquals("/index.html", response.headers.get("location"))
            }
        }
    }

    @Test
    fun testRootRedirect2() {
        withTestApplication({ main() }) {
            handleRequest(HttpMethod.Get, "").apply {
                assertEquals(HttpStatusCode.Found, response.status())
                assertEquals("/index.html", response.headers.get("location"))
            }
        }
    }

    @Test
    fun testIndexHtml() {
        withTestApplication({ main() }) {
            handleRequest(HttpMethod.Get, "/index.html").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}