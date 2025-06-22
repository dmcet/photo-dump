package de.cetvericov.photodump.images.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("images")
data class ImageEntity(
    @Id
    val id: Long? = null,
    val name: String? = null
)