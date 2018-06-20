package me.avo.io.ktor.auth.jwt.sample

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) { gson { } }

    val userSource: UserSource = UserSourceImpl()
    install(Authentication) {
        /**
         * Setup the JWT authentication to be used in [Routing].
         * If the token is valid, the corresponding [User] is fetched from the database.
         * The [User] can then be accessed in each [ApplicationCall].
         */
        jwt("jwt") {
            verifier(JwtConfig.verifier)
            realm = "ktor.io"
            validate {
                it.payload.getClaim("id").asInt()?.let(userSource::findUserById)
            }
        }
    }

    install(Routing) {

        /**
         * A public login [Route] used to obtain JWTs
         */
        post("login") {
            val credentials = call.receive<UserPasswordCredential>()
            val user = userSource.findUserByCredentials(credentials)
            val token = JwtConfig.makeToken(user)
            call.respondText(token)
        }

        /**
         * All [Route]s in the authentication block are secured.
         */
        authenticate("jwt") {
            route("secret") {

                get {
                    val user = call.user
                    call.respond(user.countries)
                }

                put {
                    TODO("All your secret routes can follow here")
                }

            }
        }
    }
}