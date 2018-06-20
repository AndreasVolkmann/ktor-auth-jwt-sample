package me.avo.io.ktor.auth.jwt.sample

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication

val ApplicationCall.user get() = authentication.principal<User>()!!

val testUser = User(1, "Test", listOf("Egypt", "Austria"))