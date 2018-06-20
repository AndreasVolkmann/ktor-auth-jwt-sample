package me.avo.io.ktor.auth.jwt.sample

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun startServer() = embeddedServer(Netty, 5000) {
    install(CallLogging)
    install(ContentNegotiation) {
        jackson { }
    }

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
}.start(true)

private val ApplicationCall.user get() = authentication.principal<User>()!!