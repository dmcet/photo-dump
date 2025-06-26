package de.cetvericov.photodump.auth.config

import de.cetvericov.photodump.auth.service.JwtService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtService: JwtService,
    private val userDetailsService: SecurityUserDetailsService
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> = Mono.justOrEmpty(authentication)
        .cast(BearerToken::class.java)
        .flatMap { jwt ->
            try {
                val username = jwtService.getUsernameFromToken(jwt.credentials)

                if (!jwtService.validateToken(jwt.credentials)) {
                    return@flatMap Mono.error(BadCredentialsException("Invalid JWT token."))
                }

                userDetailsService.findByUsername(username)
                    .map { userDetails ->
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                    }
            } catch (ex: Exception) {
                Mono.error(BadCredentialsException("Invalid JWT token."))
            }
        }
}