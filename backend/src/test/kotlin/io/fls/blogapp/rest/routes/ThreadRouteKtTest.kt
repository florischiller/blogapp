package io.fls.blogapp.rest.routes

import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.fls.blogapp.core.service.hashPassword
import io.fls.blogapp.modules.modules
import io.fls.blogapp.persistence.entities.UserDbo
import io.fls.blogapp.persistence.entities.UserThreadDbo
import io.fls.blogapp.rest.jwt.JwtUser
import io.fls.blogapp.rest.jwt.JwtVerifer
import io.fls.blogapp.rest.main
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationRequest
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
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThreadRouteKtTest : KoinTest {
    val apiPath = "/api/v1/threads/"
    val userDbo = UserDbo(
        _id = null,
        name = "Horst",
        password = hashPassword("password"),
        email = "horst@horst.com"
    )

    var database: MongoDatabase? = null
    val name = "About the Horst"
    val content = "Dieser Eintrag behandelt den unvergleichlichen Horst"

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
        saveUserInDB(userDbo)
    }

    @Test
    fun testPostThread() {
        val jwtUser = createAuthenticatedUser()
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, apiPath) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "name":"About the Horst",
                        "content":"Dieser Eintrag behandelt den unvergleichlichen Horst"
                    }
                """.trimIndent()
                )
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertThat(response.content, hasJsonPath("$.name", equalTo(name)))
                assertThat(response.content, hasJsonPath("$.content", equalTo(content)))
                assertThat(response.content, hasJsonPath("$.id", not(nullValue())))
                assertThat(response.content, hasJsonPath("$.userId", equalTo(jwtUser.id)))
                assertThat(response.content, hasJsonPath("$.userName", equalTo(jwtUser.name)))
                assertThat(response.headers.get("location"), startsWith(apiPath))
                assertThat(countAllThreads(), equalTo(1))
            }
        }
    }

    @Test
    fun testPostThreadPlichtfelderLeer() {
        val jwtUser = createAuthenticatedUser()
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, apiPath) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "name":null,
                        "content":null
                    }
                """.trimIndent()
                )
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.BadRequest)
                assertThat(
                    response.content, hasJsonPath(
                        "$.fehler",
                        equalTo("Ein Pflichtwert war leer!")
                    )
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(400)))
                assertThat(countAllThreads(), equalTo(0))
            }
        }
    }

    @Test
    fun testPostThreadInvaliderJsonString() {
        val jwtUser = createAuthenticatedUser()
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Post, apiPath) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "name":null
                        "content":null
                    }
                """.trimIndent()
                )
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.BadRequest)
                assertThat(
                    response.content, hasJsonPath(
                        "$.fehler",
                        equalTo("Es wurde ein invalider JSON-String übergeben")
                    )
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(400)))
                assertThat(countAllThreads(), equalTo(0))
            }
        }
    }

    @Test
    fun testGetThread() {
        val jwtUser = createAuthenticatedUser()
        val userThreadDbo = createDefaultUserDbo(jwtUser)
        saveUserThreadInDB(userThreadDbo)
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Get, apiPath + userThreadDbo._id) {
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.OK)
                assertThat(response.content, hasJsonPath("$.name", equalTo(name)))
                assertThat(response.content, hasJsonPath("$.content", equalTo(content)))
                assertThat(response.content, hasJsonPath("$.id", equalTo(userThreadDbo._id)))
                assertThat(response.content, hasJsonPath("$.userId", equalTo(userThreadDbo.userId)))
                assertThat(response.content, hasJsonPath("$.userName", equalTo(userThreadDbo.userName)))
            }
        }
    }

    @Test
    fun testGetThreadNotFound() {
        val jwtUser = createAuthenticatedUser()
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Get, apiPath + 1) {
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NotFound)
                assertThat(
                    response.content, hasJsonPath(
                        "$.fehler",
                        equalTo("Der Eintrag wurde nicht gefunden")
                    )
                )
                assertThat(response.content, hasJsonPath("$.status", equalTo(404)))
            }
        }
    }

    @Test
    fun testDeleteThread() {
        val jwtUser = createAuthenticatedUser()
        val userThreadDbo = createDefaultUserDbo(jwtUser)
        saveUserThreadInDB(userThreadDbo)
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Delete, apiPath + userThreadDbo._id) {
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NoContent)
                assertThat(countAllThreads(), equalTo(0))
            }
        }
    }

    @Test
    fun testDeleteThreadNotFound() {
        val jwtUser = createAuthenticatedUser()
        withTestApplication(Application::main) {
            handleRequest(HttpMethod.Delete, apiPath + 1) {
                addJwtHeader(jwtUser)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NoContent)
                assertThat(countAllThreads(), equalTo(0))
            }
        }
    }

    private fun saveUserThreadInDB(userThreadDbo: UserThreadDbo) {
        val collection = database?.getCollection<UserThreadDbo>("threads")
        collection?.save(userThreadDbo)
    }

    private fun countAllThreads() = database?.getCollection("threads")?.find()?.count()

    private fun saveUserInDB(userDbo: UserDbo) {
        val collection = get<MongoCollection<UserDbo>>(named("usersCollection"))
        collection.save(userDbo)
    }

    private fun TestApplicationRequest.addJwtHeader(user: JwtUser) =
        addHeader("Authorization", "Bearer ${getToken(user)}")

    private fun getToken(user: JwtUser) = JwtVerifer.makeToken(user)

    private fun createAuthenticatedUser(): JwtUser {
        return JwtUser(
            id = userDbo._id ?: "",
            name = "Horst",
            email = "horst@horst.com"
        )
    }

    private fun createDefaultUserDbo(jwtUser: JwtUser) =
        UserThreadDbo(
            name = name, _id = null, content = content,
            userId = jwtUser.id, userName = jwtUser.name,
            creationDate = ZonedDateTime.now()
        )
}