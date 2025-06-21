package de.cetvericov.photodump.users.api.controller

import de.cetvericov.photodump.users.api.dto.LoginRequest
import de.cetvericov.photodump.users.api.dto.LoginResponse
import de.cetvericov.photodump.users.api.dto.LogoutRequest
import de.cetvericov.photodump.users.api.dto.RegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController {

    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        // Check database for user, issue token when user exists
    }

    @PostMapping("/logout")
    suspend fun logout(@RequestBody logoutRequest: LogoutRequest): ResponseEntity<Unit> {
        // Logout user, return OK if everything was fine
    }

    @PostMapping("/register")
    suspend fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Unit> {
        // Create a user if no such user exists yet.
    }

}