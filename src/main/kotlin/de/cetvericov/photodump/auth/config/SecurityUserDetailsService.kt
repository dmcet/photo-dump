package de.cetvericov.photodump.auth.config

import de.cetvericov.photodump.users.persistence.repository.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SecurityUserDetailsService(
    private val userRepository: UserRepository
) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> =
        userRepository.findByUsername(username).map { user ->
            User.builder()
                .username(user.username)
                .password(user.password)
                .roles("USER")
                .build()
        }
}