package de.cetvericov.photodump.security

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
    /**
         * Authenticates a user based on a JWT bearer token.
         *
         * Extracts the username from the provided JWT, retrieves user details, and validates the token.
         * Returns an authenticated token with user authorities if successful, or an error if authentication fails.
         *
         * @param authentication The authentication request containing the JWT bearer token.
         * @return A Mono emitting the authenticated token or an error if the JWT is invalid.
         */
        override fun authenticate(authentication: Authentication): Mono<Authentication> = Mono.justOrEmpty(authentication)
        .cast(BearerToken::class.java)
        .flatMap { jwt ->
            try {
                val username = jwtService.getUsernameFromToken(jwt.credentials)
                userDetailsService.findByUsername(username).filter { jwtService.validateToken(jwt.credentials) }
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