package de.cetvericov.photodump.api.dto

import de.cetvericov.photodump.persistence.entity.ImageEntity

data class ImageDto(
    val id: Long?,
    val name: String
) {
    companion object {
        fun fromEntity(image: ImageEntity) = ImageDto(
            id = image.id,
            name = image.name ?: ""
        )
    }
}
