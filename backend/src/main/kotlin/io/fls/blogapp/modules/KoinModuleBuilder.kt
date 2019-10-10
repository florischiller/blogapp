package io.fls.blogapp.modules

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Indexes
import com.typesafe.config.ConfigFactory
import io.fls.blogapp.core.ports.ThreadPersistencePort
import io.fls.blogapp.core.ports.UserPersistencePort
import io.fls.blogapp.core.service.ThreadService
import io.fls.blogapp.core.service.ThreadServiceImpl
import io.fls.blogapp.core.service.UserService
import io.fls.blogapp.core.service.UserServiceImpl
import io.fls.blogapp.persistence.MongoDbThreadPersistenceAdapterImpl
import io.fls.blogapp.persistence.MongoDbUserPersistenceAdapterImpl
import io.fls.blogapp.persistence.entities.UserDbo
import io.github.config4k.extract
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

data class MongoDbConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val adminDatabase: String = "admin",
    val database: String = "blog"
)

val modules = module(createdAtStart = true) {
    // Services
    single<ThreadService> { ThreadServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    // Repositories
    single<ThreadPersistencePort> {
        MongoDbThreadPersistenceAdapterImpl(get())
    }
    single<UserPersistencePort> {
        MongoDbUserPersistenceAdapterImpl(get(named("usersCollection")))
    }
    // Repositories
    single<MongoDatabase> {
        val config = ConfigFactory.load()
        val mongodbConfig = config.extract<MongoDbConfig>("mongodb")

        val client = KMongo.createClient(
            ServerAddress(mongodbConfig.host, mongodbConfig.port),
            listOf(createMongoCredential(mongodbConfig))
        )
        client.getDatabase(mongodbConfig.database)
    }

    single(named("usersCollection")) {
        val database: MongoDatabase = get()
        val col = database.getCollection<UserDbo>("users")
        if (!col.listIndexes().contains(Indexes.ascending("name"))) {
            col.createIndex(Indexes.ascending("name"))
        }
        col
    }
}

private fun createMongoCredential(mongodbConfig: MongoDbConfig): MongoCredential {
    return MongoCredential.createCredential(
        mongodbConfig.user, mongodbConfig.adminDatabase, mongodbConfig.password.toCharArray()
    )
}