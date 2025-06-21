package de.cetvericov.photodump.images.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.Objects

@Table("images")
data class ImageEntity(
    @Id
    var id: Long? = null,
    var name: String? = null
)