package de.cetvericov.photodump.images.persistence.repository

import de.cetvericov.photodump.images.persistence.entity.ImageEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ImageRepository: ReactiveCrudRepository<ImageEntity, Long>