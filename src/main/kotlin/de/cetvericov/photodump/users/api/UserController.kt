package de.cetvericov.photodump.users.api

import de.cetvericov.photodump.auth.service.JwtService
import de.cetvericov.photodump.security.SecurityUserDetailsService
import de.cetvericov.photodump.users.api.dto.LoginRequest
import de.cetvericov.photodump.users.api.dto.LoginResponse
import de.cetvericov.photodump.users.api.dto.RegisterRequest
import de.cetvericov.photodump.users.persistence.entity.UserEntity
import de.cetvericov.photodump.users.persistence.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userDetailsService: SecurityUserDetailsService

) {
    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> =
        userDetailsService.findByUsername(loginRequest.username)
            .filter { user -> passwordEncoder.matches(loginRequest.password, user.password) }
            .map { user ->
                ResponseEntity.ok(LoginResponse(jwtService.generateToken(user)))
            }
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
            .awaitSingle()


    @PostMapping("/register")
    suspend fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Unit> {
        if (registerRequest.password != registerRequest.passwordRepeated) {
            return ResponseEntity.badRequest().build()
        }

        return userRepository.findByUsername(registerRequest.username)
            .map<ResponseEntity<Unit>> { ResponseEntity.badRequest().build() }
            .switchIfEmpty(
                Mono.defer {
                    userRepository.save(
                        UserEntity(
                            username = registerRequest.username,
                            password = passwordEncoder.encode(registerRequest.password)
                        )
                    )
                }.map { ResponseEntity.ok().build() }
            ).awaitSingle()
    }

}