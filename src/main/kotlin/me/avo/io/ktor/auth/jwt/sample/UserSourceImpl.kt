package me.avo.io.ktor.auth.jwt.sample

import io.ktor.auth.*

class UserSourceImpl : UserSource {

    override fun findUserById(id: Int): User = users.getValue(id)

    override fun findUserByCredentials(credential: UserPasswordCredential): User = testUser

    private val users = listOf(testUser).associateBy(User::id)

}