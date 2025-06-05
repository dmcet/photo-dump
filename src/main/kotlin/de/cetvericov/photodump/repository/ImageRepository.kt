package de.cetvericov.photodump.repository

import de.cetvericov.photodump.model.Image
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ImageRepository: ReactiveCrudRepository<Image, Long>