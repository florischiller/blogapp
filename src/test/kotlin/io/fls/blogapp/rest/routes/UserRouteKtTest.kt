package io.fls.blogapp.rest.routes

import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.fls.blogapp.core.service.hashPassword
import io.fls.blogapp.modules.modules
import io.fls.blogapp.persistence.entities.UserDbo
import io.fls.blogapp.rest.main
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.litote.kmongo.save

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRouteKtTest : KoinTest {

    var database: MongoDatabase? = null
    val name = "horst"
    val email = "real@horst.com"

    @BeforeAll
    fun beforeAll() {
        startKoin { modules(modules) }
        val tempDatabase: MongoDatabase by inject()
        database = tempDatabase
    }

    @AfterAll
    fun autoClose() {
        stopKoin()
    }

    @BeforeEach
    fun before() {
        database?.drop()
    }

    @Test
    fun testCreateUser() {
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, "/api/v1/users") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "name": "horst",
                        "password": "password",
                        "email":"real@horst.com"
                    }
                """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertThat(response.content, hasJsonPath("$.name", equalTo(name)))
                assertThat(response.content, hasJsonPath("$.email", equalTo(email)))
                assertThat(response.content, hasJsonPath("$.id", not(nullValue())))
                assertThat(response.headers.get("location"), startsWith("/api/v1/users"))
            }
        }
    }

    @Test
    fun testCreateUserWithExistingUsername() {
        val userDbo = UserDbo(
            _id = null,
            name = name,
            password = hashPassword("password"),
            email = email
        )
        saveUserInDB(userDbo)

        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, "/api/v1/users") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "name": "horst",
                        "password": "password",
                        "email":"real@horst.com"
                    }
                """.trimIndent()
                )
            }.apply {
                assertEquals(response.status(), HttpStatusCode.Conflict)
                assertThat(
                    response.content, hasJsonPath("$.fehler", equalTo("Der Nutzer exisitert bereits"))
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(409)))
            }
        }
    }

    @Test
    fun testCreateJwt() {
        val userDbo = UserDbo(
            _id = null,
            name = name,
            password = hashPassword("password"),
            email = email
        )
        saveUserInDB(userDbo)

        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, "/api/v1/users/${name}/jwt") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "password": "password"
                    }
                """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThat(response.content, hasJsonPath("$.jwt", not(nullValue())))
            }
        }
    }

    @Test
    fun testCreateJwtWrongPassword() {
        val userDbo = UserDbo(
            _id = null,
            name = name,
            password = hashPassword("password"),
            email = email
        )
        saveUserInDB(userDbo)

        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, "/api/v1/users/${name}/jwt") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "password": "kein_password"
                    }
                """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.Forbidden, response.status())
                assertThat(
                    response.content, hasJsonPath(
                        "$.fehler",
                        equalTo("Die Anfrage wurde aus ermangelung von Rechten nicht ausgeführt")
                    )
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(403)))
            }
        }
    }

    @Test
    fun testGetUser() {
        val userDbo = UserDbo(
            _id = null,
            name = name,
            password = hashPassword("password"),
            email = email
        )
        saveUserInDB(userDbo)

        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Get, "/api/v1/users/${name}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThat(response.content, hasJsonPath("$.name", equalTo(name)))
                assertThat(response.content, hasJsonPath("$.email", equalTo(email)))
                assertThat(response.content, hasJsonPath("$.id", not(nullValue())))
            }
        }
    }

    @Test
    fun testGetUserWithoutUserInDb() {
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Get, "/api/v1/users/${name}").apply {
                assertEquals(response.status(), HttpStatusCode.NotFound)
                assertThat(
                    response.content, hasJsonPath(
                        "$.fehler",
                        equalTo("Der übergeben Nutzername war leer")
                    )
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(404)))
            }
        }
    }

    private fun saveUserInDB(userDbo: UserDbo) {
        val collection = get<MongoCollection<UserDbo>>(named("usersCollection"))
        collection.save(userDbo)
    }
}