package de.cetvericov.photodump.persistence.repository

import de.cetvericov.photodump.persistence.entity.ImageEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ImageRepository: ReactiveCrudRepository<ImageEntity, Long>