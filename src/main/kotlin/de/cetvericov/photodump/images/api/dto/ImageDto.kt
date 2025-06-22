package de.cetvericov.photodump.images.api.dto

import de.cetvericov.photodump.images.persistence.entity.ImageEntity

data class ImageDto(
    val id: Long?,
    val name: String,
    val url: String
) {
    companion object {
        fun fromEntity(image: ImageEntity) = ImageDto(
            id = image.id,
            name = image.name ?: "",
            url = "/api/v1/images/${image.id}"
        )
    }
}
