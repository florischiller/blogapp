package io.fls.blogapp.persistence

import com.mongodb.client.MongoDatabase
import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.ports.UserPersistencePort
import io.fls.blogapp.persistence.entities.UserDbo
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoDbUserPersistenceAdapterImpl(
    private var database: MongoDatabase
) : UserPersistencePort {
    override fun save(user: User): User {
        val col = database.getCollection<UserDbo>("users")
        val dboToSave = transformToDbo(user)
        col.save(dboToSave)

        return transformToModel(dboToSave)
    }

    override fun findById(id: String): User? {
        val col = database.getCollection<UserDbo>("users")
        val foundUser = col.findOneById(id)

        return if (foundUser != null) transformToModel(foundUser)
        else null
    }

    private fun transformToModel(user: UserDbo): User =
        User(name = user.name, id = user._id, email = user.email, password = user.password)

    private fun transformToDbo(user: User): UserDbo =
        UserDbo(name = user.name, _id = user.id, email = user.email, password = user.password)
}