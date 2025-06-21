package de.cetvericov.photodump.users.persistence.repository

import de.cetvericov.photodump.users.persistence.entity.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono


interface UserRepository: ReactiveCrudRepository<UserEntity, Long> {
    /**
 * Retrieves a user entity matching the specified username.
 *
 * @param username The username to search for.
 * @return A Mono emitting the user entity if found, or empty if no user matches the username.
 */
fun findByUsername(username: String): Mono<UserEntity>
}