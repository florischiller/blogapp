package io.fls.blogapp.persistence

import com.mongodb.client.MongoDatabase
import io.fls.blogapp.core.model.UserThread
import io.fls.blogapp.core.ports.ThreadPersistencePort
import io.fls.blogapp.persistence.entities.UserThreadDbo
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoDbThreadPersistenceAdapterImpl(
    private var database: MongoDatabase
) : ThreadPersistencePort {
    override fun delete(id: String) {
        val col = database.getCollection<UserThreadDbo>("threads")
        col.deleteOneById(id)
    }

    override fun findById(id: String): UserThread? {
        val col = database.getCollection<UserThreadDbo>("threads")
        val foundThread = col.findOneById(id)

        return if (foundThread != null) transformToModel(foundThread)
        else null
    }

    override fun create(thread: UserThread): UserThread {
        val col = database.getCollection<UserThreadDbo>("threads")
        val threadDbo = transformToDbo(thread)
        col.save(threadDbo)
        return transformToModel(threadDbo)
    }

    private fun transformToDbo(thread: UserThread) =
        UserThreadDbo(
            name = thread.name, _id = null, content = thread.content,
            userId = thread.userId, userName = thread.userName,
            creationDate = thread.creationDate
        )

    private fun transformToModel(thread: UserThreadDbo): UserThread =
        UserThread(
            name = thread.name,
            id = thread._id,
            content = thread.content,
            userId = thread.userId,
            userName = thread.userName,
            creationDate = thread.creationDate
        )
}