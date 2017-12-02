package me.avo.io.ktor.auth.jwt.sample

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun startServer() = embeddedServer(Netty, 5000) {

    val userSource: UserSource = UserSourceImpl()

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

        route("secret") {

            /**
             * A secret [Route], which requires a valid JWT.
             * If the token is valid, the corresponding [User] is fetched from the database.
             * The [User] can then be accessed in each [ApplicationCall].
             */
            authentication {
                jwtAuthentication(JwtConfig.verifier, "ktor.io") {
                    it.payload.getClaim("id").asInt()?.let(userSource::findUserById)
                }
            }

            /**
             * All [Route]s following the authentication block are secured.
             */
            get {
                val user = call.user
                call.respond(user.countries)
            }

            put {
                TODO("All your secret routes can follow here")
            }

        }

    }

}.start(true)

private val ApplicationCall.user get() = authentication.principal<User>()!!