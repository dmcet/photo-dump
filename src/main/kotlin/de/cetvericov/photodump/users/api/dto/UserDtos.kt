package de.cetvericov.photodump.users.api.dto

import de.cetvericov.photodump.auth.Token

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: Token
)

data class LogoutRequest(
    val userName: String,
    val token: Token
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val passwordRepeated: String
)