package me.avo.io.ktor.auth.jwt.sample

import io.ktor.auth.*

class UserSourceImpl: UserSource {

    override fun findUserById(id: Int): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findUserByCredentials(credential: UserPasswordCredential): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}