package de.cetvericov.photodump.users.persistence.repository

import de.cetvericov.photodump.users.persistence.entity.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository


interface UserRepository: ReactiveCrudRepository<UserEntity, Long>