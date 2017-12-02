# Ktor Authentication using Json Web Token

A simple but slightly more elaborate example of how to include JWT in the Ktor application flow. 




`Server.kt` contains the server setup, including the `Routing`.

There is a public login route, which responds with JWTs. 

These are used to access the secret route. 

The JWT configuration can be found in `JwtConfig.kt`.
