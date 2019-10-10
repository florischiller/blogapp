package io.fls.blogapp.persistence

import com.mongodb.client.MongoCollection
import io.fls.blogapp.core.model.User
import io.fls.blogapp.core.ports.UserPersistencePort
import io.fls.blogapp.persistence.entities.UserDbo
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.save

class MongoDbUserPersistenceAdapterImpl(
    private var collection: MongoCollection<UserDbo>
) : UserPersistencePort {
    override fun save(user: User): User {
        val dboToSave = transformToDbo(user)
        collection.save(dboToSave)

        return transformToModel(dboToSave)
    }

    override fun findByName(name: String): User? {
        val foundUser = collection.findOne(UserDbo::name eq name)

        return if (foundUser != null) transformToModel(foundUser)
        else null
    }

    private fun transformToModel(user: UserDbo): User =
        User(name = user.name, id = user._id, email = user.email, password = user.password)

    private fun transformToDbo(user: User): UserDbo =
        UserDbo(name = user.name, _id = user.id, email = user.email, password = user.password)
}