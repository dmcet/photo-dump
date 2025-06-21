package de.cetvericov.photodump.users.api.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val passwordRepeated: String
)