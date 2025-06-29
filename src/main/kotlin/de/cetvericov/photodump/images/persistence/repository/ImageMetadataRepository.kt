package de.cetvericov.photodump.images.persistence.repository

import de.cetvericov.photodump.images.persistence.entity.ImageMetadataEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ImageMetadataRepository: ReactiveCrudRepository<ImageMetadataEntity, Long> {
    fun findByOwnerId(ownerId: Long): Flux<ImageMetadataEntity>
}