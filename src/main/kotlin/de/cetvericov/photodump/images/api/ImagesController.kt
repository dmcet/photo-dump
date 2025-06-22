package de.cetvericov.photodump.images.api

import de.cetvericov.photodump.images.api.dto.ImageDto
import de.cetvericov.photodump.images.persistence.entity.ImageMetadataEntity
import de.cetvericov.photodump.images.persistence.repository.ImageMetadataRepository
import de.cetvericov.photodump.images.persistence.service.ImageStoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImagesController(
    private val imageMetadataRepository: ImageMetadataRepository,
    private val imageStoreService: ImageStoreService
) {
    @GetMapping
    fun getImages(): Flow<ImageDto> = imageMetadataRepository.findAll().map(ImageDto.Companion::fromEntity).asFlow()

    @GetMapping("/{id}")
    suspend fun getImageData(@PathVariable id: Long): ResponseEntity<ByteArray> {

        val imageOrNull =
            imageMetadataRepository.findById(id).awaitFirstOrNull() ?: return ResponseEntity.notFound().build()
        val imageName = imageOrNull.name ?: return ResponseEntity.notFound().build()
        val imageBytes = imageStoreService.getImage(imageName) ?: return ResponseEntity.notFound().build()

        val mediaType = determineMediaType(imageOrNull.name)

        return ResponseEntity.ok().contentType(mediaType)
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(imageBytes)
    }

    private fun determineMediaType(name: String): MediaType = when {
        name.endsWith(".jpg", true)
                || name.endsWith(".jpeg", true) -> MediaType.IMAGE_JPEG

        name.endsWith(".png", true) -> MediaType.IMAGE_PNG
        name.endsWith(".gif", true) -> MediaType.IMAGE_GIF
        else -> MediaType.APPLICATION_OCTET_STREAM
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

        imageStoreService.saveImage(filePart.filename(), bytes)

        val imageMetadataEntity = ImageMetadataEntity(
            name = filePart.filename()
        )

        val savedImage = imageMetadataRepository.save(imageMetadataEntity).awaitSingle()

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteImage(@PathVariable id: Long) {
        if (!imageMetadataRepository.existsById(id).awaitSingle()) {
            return
        }

        imageStoreService.deleteImage(imageMetadataRepository.findById(id).awaitFirstOrNull()?.name!!)
        imageMetadataRepository.deleteById(id).awaitFirstOrNull()
    }
}