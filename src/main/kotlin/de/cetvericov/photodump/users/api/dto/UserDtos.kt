package de.cetvericov.photodump.users.api.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @NotBlank(message = "Username is required")
    val username: String,
    @NotBlank(message = "Password is required")
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    @NotBlank(message = "Username is required")
    val username: String,
    @NotBlank(message = "Password is required")
    val password: String,
    @NotBlank(message = "Password repetition is required")
    val passwordRepeated: String
)