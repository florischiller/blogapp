package io.fls.blogapp.persistence

import com.mongodb.client.MongoCollection
import io.fls.blogapp.core.model.UserThread
import io.fls.blogapp.core.ports.ThreadPersistencePort
import io.fls.blogapp.persistence.entities.UserThreadDbo
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.save
import java.util.stream.Stream

class MongoDbThreadPersistenceAdapterImpl(
    private var collection: MongoCollection<UserThreadDbo>
) : ThreadPersistencePort {
    override fun findAll(limit: Int, offset: Int): Stream<UserThread>? {
        val foundThreads: Stream<UserThreadDbo> = collection.find().limit(limit).skip(offset)
            .toSet().stream()

        return foundThreads.map { transformToModel(it) }
    }

    override fun delete(id: String) {
        collection.deleteOneById(id)
    }

    override fun findById(id: String): UserThread? {
        val foundThread = collection.findOneById(id)

        return if (foundThread != null) transformToModel(foundThread)
        else null
    }

    override fun create(thread: UserThread): UserThread {
        val threadDbo = transformToDbo(thread)
        collection.save(threadDbo)
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