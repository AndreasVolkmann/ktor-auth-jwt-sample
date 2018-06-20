# Ktor Authentication using Json Web Token

A simple but slightly more elaborate example of how to include JWT in the Ktor application flow. 




[Module.kt](/src/main/kotlin/me/avo/io/ktor/auth/jwt/sample/Module.kt) contains the server setup, including the `Routing`.

There is a public login route, which responds with JWTs. 

These are used to access the secret route. 

The JWT configuration can be found in [JwtConfig.kt](/src/main/kotlin/me/avo/io/ktor/auth/jwt/sample/JwtConfig.kt).

Tests can be found in [ServerTest.kt](/src/test/kotlin/me/avo/io/ktor/auth/jwt/sample/ServerTest.kt)
