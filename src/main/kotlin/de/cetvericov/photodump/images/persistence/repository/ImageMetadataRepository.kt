package de.cetvericov.photodump.images.persistence.repository

import de.cetvericov.photodump.images.persistence.entity.ImageMetadataEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ImageMetadataRepository: ReactiveCrudRepository<ImageMetadataEntity, Long>