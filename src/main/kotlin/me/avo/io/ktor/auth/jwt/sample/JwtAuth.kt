package me.avo.io.ktor.auth.jwt.sample

import com.auth0.jwt.*
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.impl.*
import com.auth0.jwt.interfaces.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*

private val JWTAuthKey: Any = "JWTAuth"

class JWTCredential(val payload: Payload) : Credential
class JWTPrincipal(val payload: Payload) : Principal

/**
 * Add JWT token authentication to the pipeline using a [JWTVerifier] to verify the token integrity.
 * @param [jwtVerifier] verifies token format and signature
 * @param [realm] used in the WWW-Authenticate response header
 * @param [validate] verify the credentials provided by the client token
 */
fun AuthenticationPipeline.jwtAuthentication(jwtVerifier: JWTVerifier, realm: String, validate: (JWTCredential) -> Principal?) {
    intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val token = call.request.parseAuthorizationHeaderOrNull()
        val principal = verifyAndValidate(jwtVerifier, token, validate)
        evaluate(token, principal, realm, context)
    }
}


private suspend fun evaluate(token: HttpAuthHeader?, principal: Principal?, realm: String, context: AuthenticationContext) {
    val cause = when {
        token == null -> NotAuthenticatedCause.NoCredentials
        principal == null -> NotAuthenticatedCause.InvalidCredentials
        else -> null
    }
    if (cause != null) {
        context.challenge(JWTAuthKey, cause) {
            call.respond(UnauthorizedResponse(HttpAuthHeader.bearerAuthChallenge(realm)))
            it.success()
        }
    }
    if (principal != null) {
        context.principal(principal)
    }
}


private fun verifyAndValidate(jwtVerifier: JWTVerifier?, token: HttpAuthHeader?, validate: (JWTCredential) -> Principal?): Principal? {
    val jwt = try {
        token.getBlob()?.let { jwtVerifier?.verify(it) }
    } catch (ex: JWTVerificationException) {
        null
    } ?: return null

    val payload = jwt.parsePayload()
    val credentials = payload.let(::JWTCredential)
    return credentials.let(validate)
}

private fun HttpAuthHeader?.getBlob() = when {
    this is HttpAuthHeader.Single && authScheme == "Bearer" -> blob
    else -> null
}

private fun ApplicationRequest.parseAuthorizationHeaderOrNull() = try {
    parseAuthorizationHeader()
} catch (ex: IllegalArgumentException) {
    null
}

private fun HttpAuthHeader.Companion.bearerAuthChallenge(realm: String): HttpAuthHeader =
        HttpAuthHeader.Parameterized("Bearer", mapOf(HttpAuthHeader.Parameters.Realm to realm))


private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = kotlin.text.String(java.util.Base64.getUrlDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}