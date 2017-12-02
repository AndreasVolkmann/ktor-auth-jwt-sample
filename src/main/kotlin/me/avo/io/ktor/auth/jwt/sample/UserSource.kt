package me.avo.io.ktor.auth.jwt.sample

import io.ktor.auth.*

interface UserSource {

    fun findUserById(id: Int): User

    fun findUserByCredentials(credential: UserPasswordCredential): User

}