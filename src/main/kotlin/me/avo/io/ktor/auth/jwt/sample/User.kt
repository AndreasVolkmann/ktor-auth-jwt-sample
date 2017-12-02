package me.avo.io.ktor.auth.jwt.sample

import io.ktor.auth.*

data class User(
        val id: Int,
        val name: String,
        val countries: List<String>
) : Principal