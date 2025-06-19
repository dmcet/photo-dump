package de.cetvericov.photodump.api.controller

import de.cetvericov.photodump.api.dto.ImageDto
import de.cetvericov.photodump.persistence.entity.ImageEntity
import de.cetvericov.photodump.persistence.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImagesController(private val imageRepository: ImageRepository) {
    @GetMapping
    fun getImages(): Flow<ImageDto> = imageRepository.findAll().map(ImageDto::fromEntity).asFlow()

    @GetMapping("/{id}")
    suspend fun getImageData(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val imageOrNull = imageRepository.findById(id).awaitFirstOrNull() ?: return ResponseEntity.notFound().build()

        val contentType = when {
            imageOrNull.name?.endsWith(".jpg", true) == true ||
                    imageOrNull.name?.endsWith(".jpeg", true) == true -> MediaType.IMAGE_JPEG

            imageOrNull.name?.endsWith(".png", true) == true -> MediaType.IMAGE_PNG
            imageOrNull.name?.endsWith(".gif", true) == true -> MediaType.IMAGE_GIF
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        return ResponseEntity.ok().contentType(contentType)
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(imageOrNull.data)
    }

    @PostMapping("upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
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
            .location(URI.create("/api/v1/images/${savedImage.id}"))
            .build()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteImage(@PathVariable id: Long) = imageRepository.deleteById(id).awaitFirstOrNull()
}