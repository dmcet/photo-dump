package de.cetvericov.photodump.images.api

import de.cetvericov.photodump.images.api.dto.ImageDto
import de.cetvericov.photodump.images.persistence.entity.ImageMetadataEntity
import de.cetvericov.photodump.images.persistence.repository.ImageMetadataRepository
import de.cetvericov.photodump.images.persistence.service.ImageStoreService
import de.cetvericov.photodump.users.persistence.repository.UserRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImagesController(
    private val imageMetadataRepository: ImageMetadataRepository,
    private val imageStoreService: ImageStoreService,
    private val userRepository: UserRepository
) {
    @GetMapping
    suspend fun getImages(): ImageDto =
        imageMetadataRepository.findAll().map(ImageDto::fromEntity).awaitSingle()

    @GetMapping("/{id}")
    suspend fun getImageData(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val imageOrNull =
            imageMetadataRepository.findById(id).awaitFirstOrNull() ?: return ResponseEntity.notFound().build()

        val imageBytes =
            imageStoreService.getImage(imageOrNull.name) ?: return ResponseEntity.notFound().build()

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
        @RequestPart("file") filePart: FilePart,
        authentication: Authentication
    ): ResponseEntity<Unit> {

        val bytes = filePart.content().map { buffer ->
            val bytes = ByteArray(buffer.readableByteCount())
            buffer.read(bytes)
            bytes
        }.reduce { acc, bytes -> acc + bytes }.awaitSingle()

        imageStoreService.saveImage(filePart.filename(), bytes)

        val imageMetadataEntity = ImageMetadataEntity(
            name = filePart.filename(),
            ownerId = userRepository.findByUsername(authentication.principal.toString()).awaitSingle().id!!
        )

        imageMetadataRepository.save(imageMetadataEntity).awaitSingle()

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteImage(@PathVariable id: Long, authentication: Authentication): ResponseEntity<Unit> {
        if (!isImageOwner(id, authentication.principal.toString())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        if (!imageExists(id)) {
            return ResponseEntity.notFound().build()
        }

        imageStoreService.deleteImage(imageMetadataRepository.findById(id).awaitFirstOrNull()?.name!!)
        imageMetadataRepository.deleteById(id).awaitFirstOrNull()

        return ResponseEntity.ok().build()
    }

    private suspend fun imageExists(id: Long): Boolean = imageMetadataRepository.existsById(id).awaitSingle()

    private suspend fun isImageOwner(id: Long, username: String): Boolean {
        if (!imageExists(id)) {
            return false
        }

        val imageOwnerMono = imageMetadataRepository.findById(id)
        val tokenOwnerMono = userRepository.findByUsername(username)

        return imageOwnerMono.awaitSingle().id == tokenOwnerMono.awaitSingle().id!!
    }
}