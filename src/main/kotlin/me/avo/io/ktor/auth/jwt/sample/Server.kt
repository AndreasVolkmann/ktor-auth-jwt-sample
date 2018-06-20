package me.avo.io.ktor.auth.jwt.sample

import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun startServer() = embeddedServer(CIO, 5000) { module() }.start(true)

