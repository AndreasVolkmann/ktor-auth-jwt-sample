package me.avo.io.ktor.auth.jwt.sample

import com.google.gson.Gson
import io.ktor.auth.UserPasswordCredential
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class ServerTest {

    @Test fun `login should succeed with token`() = withServer {
        val req = handleRequest {
            method = HttpMethod.Post
            uri = "/login"
            addHeader("Content-Type", "application/json")
            setBody(
                Gson().toJson(UserPasswordCredential("user", "pass"))
            )
        }

        req.requestHandled shouldBe true
        req.response.status() shouldEqual HttpStatusCode.OK
        req.response.content.shouldNotBeNullOrBlank().length shouldBeGreaterThan 6
    }

    @Test fun `request without token should fail`() = withServer {
        val req = handleRequest {
            uri = "/secret"
        }
        req.requestHandled shouldBe true
        req.response.status() shouldEqual HttpStatusCode.Unauthorized
    }

    @Test fun `request with token should pass`() = withServer {
        val req = handleRequest {
            uri = "/secret"
            addJwtHeader()
        }
        req.requestHandled shouldBe true
        req.response.let {
            it.status() shouldEqual HttpStatusCode.OK
            it.content.shouldNotBeNullOrBlank()
        }
    }

    @Test fun `optional route should work without token`() = withServer {
        val req = handleRequest {
            uri = "/optional"
        }
        req.let {
            it.requestHandled.shouldBeTrue()
            it.response.status() shouldEqual HttpStatusCode.OK
            it.response.content.shouldNotBeNullOrBlank() shouldBeEqualTo "optional"
        }
    }

    @Test fun `optional route should work with token`() = withServer {
        val req = handleRequest {
            uri = "/optional"
            addJwtHeader()
        }
        req.let {
            it.requestHandled.shouldBeTrue()
            it.response.status() shouldEqual HttpStatusCode.OK
            it.response.content.shouldNotBeNullOrBlank() shouldBeEqualTo "authenticated!"
        }
    }

    private fun TestApplicationRequest.addJwtHeader() = addHeader("Authorization", "Bearer ${getToken()}")

    private fun getToken() = JwtConfig.makeToken(testUser)

    private fun withServer(block: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module() }, block)
    }

}