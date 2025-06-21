package de.cetvericov.photodump.security

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
    /**
         * Retrieves user details for authentication by username.
         *
         * Looks up a user by username from the repository and maps the result to a Spring Security `UserDetails` object with the role "USER".
         *
         * @param username The username to search for.
         * @return A `Mono` emitting the user details if found, or empty if no user exists with the given username.
         */
        override fun findByUsername(username: String): Mono<UserDetails> =
        userRepository.findByUsername(username).map { user ->
            User.builder()
                .username(user.username)
                .password(user.password)
                .roles("USER")
                .build()
        }
}