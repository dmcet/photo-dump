package de.cetvericov.photodump.images.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("images")
data class ImageMetadataEntity(
    @Id
    val id: Long? = null,
    val name: String,
    @Column("owner_id")
    val ownerId: Long
)