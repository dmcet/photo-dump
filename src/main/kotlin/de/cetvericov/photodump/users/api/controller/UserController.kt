package de.cetvericov.photodump.users.api.controller

import de.cetvericov.photodump.auth.service.TokenService
import de.cetvericov.photodump.users.api.dto.LoginRequest
import de.cetvericov.photodump.users.api.dto.LoginResponse
import de.cetvericov.photodump.users.api.dto.LogoutRequest
import de.cetvericov.photodump.users.api.dto.RegisterRequest
import de.cetvericov.photodump.users.persistence.entity.UserEntity
import de.cetvericov.photodump.users.persistence.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController(
    private val tokenService: TokenService,
    private val userRepository: UserRepository
) {

    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val maybeUser = userRepository.findByUsername(loginRequest.username).awaitSingleOrNull()
        if (maybeUser == null) {
            return ResponseEntity.notFound().build()
        }
        if (maybeUser.password != loginRequest.password) {
            return ResponseEntity.badRequest().build()
        }

        val token = tokenService.issueToken(loginRequest.username)
        return ResponseEntity.ok(LoginResponse(token))
    }

    @PostMapping("/logout")
    suspend fun logout(@RequestBody logoutRequest: LogoutRequest): ResponseEntity<Unit> {
        val maybeUser = userRepository.findByUsername(logoutRequest.username).awaitSingleOrNull()
        if (maybeUser == null) {
            return ResponseEntity.notFound().build()
        }

        tokenService.revokeToken(logoutRequest.username, logoutRequest.token)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/register")
    suspend fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Unit> {
        val maybeUser = userRepository.findByUsername(registerRequest.username).awaitSingleOrNull()
        if (maybeUser != null) {
            return ResponseEntity.badRequest().build()
        }

        if (registerRequest.password != registerRequest.passwordRepeated) {
            return ResponseEntity.badRequest().build()
        }

        userRepository
            .save(UserEntity(username = registerRequest.username, password = registerRequest.password))
            .awaitSingleOrNull()

        return ResponseEntity.ok().build()
    }

}