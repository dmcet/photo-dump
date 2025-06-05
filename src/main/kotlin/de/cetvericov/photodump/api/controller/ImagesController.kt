package de.cetvericov.photodump.api.controller

import de.cetvericov.photodump.api.dto.ImageDto
import de.cetvericov.photodump.persistence.entity.ImageEntity
import de.cetvericov.photodump.persistence.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/images")
class ImagesController(private val imageRepository: ImageRepository) {

    @GetMapping
    fun getImages(): Flow<ImageDto> = imageRepository.findAll().map(ImageDto::fromEntity).asFlow()


    @GetMapping("/{id}")
    suspend fun getImageData(@PathVariable id: Long): ByteArray? {
        val imageOrNull = imageRepository.findById(id).awaitFirstOrNull()

        if (imageOrNull == null) {
            return null
        }

        return imageOrNull.data
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadImage(
        @RequestPart("file") filePart: FilePart
    ): ResponseEntity<Unit> {

        val bytes = filePart.content().map { buffer ->
            val bytes = ByteArray(buffer.readableByteCount())
            buffer.read(bytes)
            bytes
        }.reduce { acc, bytes -> acc + bytes }.awaitSingle()

        val imageEntity = ImageEntity(
            name = filePart.filename(),
            data = bytes
        )

        val savedImage = imageRepository.save(imageEntity).awaitSingle()

        return ResponseEntity
            .status(HttpStatus.SEE_OTHER)
            .location(URI.create("/images/${savedImage.id}"))
            .build()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteImage(@PathVariable id: Long) = imageRepository.deleteById(id).awaitFirstOrNull()
}