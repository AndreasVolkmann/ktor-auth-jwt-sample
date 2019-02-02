package me.avo.io.ktor.auth.jwt.sample

import com.google.gson.Gson
import io.ktor.auth.UserPasswordCredential
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNullOrBlank
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
            addHeader("Authorization", "Bearer ${getToken()}")
        }
        req.requestHandled shouldBe true
        req.response.let {
            it.status() shouldEqual HttpStatusCode.OK
            it.content.shouldNotBeNullOrBlank()
        }
    }

    private fun getToken() = JwtConfig.makeToken(testUser)

    private fun withServer(block: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module() }, block)
    }

}