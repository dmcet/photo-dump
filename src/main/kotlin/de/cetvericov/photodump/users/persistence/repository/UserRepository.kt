package de.cetvericov.photodump.users.persistence.repository

import de.cetvericov.photodump.users.persistence.entity.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono


interface UserRepository: ReactiveCrudRepository<UserEntity, Long> {
    fun findByUsername(username: String): Mono<UserEntity>
}