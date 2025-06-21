package de.cetvericov.photodump.security

import de.cetvericov.photodump.auth.service.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtAuthenticationManager: JwtAuthenticationManager
) : AuthenticationWebFilter(jwtAuthenticationManager) {
    init {
        setServerAuthenticationConverter { exchange ->
            Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
                .filter { it.startsWith("Bearer ") }
                .map { it.substring(7) }
                .map { token -> BearerToken(token) }
        }
    }
}