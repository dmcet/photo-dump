package de.cetvericov.photodump.images.api

import de.cetvericov.photodump.images.dto.ImageDto
import de.cetvericov.photodump.images.persistence.entity.ImageMetadataEntity
import de.cetvericov.photodump.images.persistence.repository.ImageMetadataRepository
import de.cetvericov.photodump.images.persistence.service.ImageStoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImagesController(private val imageMetadataRepository: ImageMetadataRepository, private val imageStoreService: ImageStoreService) {
    @GetMapping
    fun getImages(): Flow<ImageDto> = imageMetadataRepository.findAll().map(ImageDto.Companion::fromEntity).asFlow()

    @GetMapping("/{id}")
    suspend fun getImageData(@PathVariable id: Long): ResponseEntity<ByteArray> {

        val imageOrNull = imageMetadataRepository.findById(id).awaitFirstOrNull() ?: return ResponseEntity.notFound().build()
        val imageName = imageOrNull.name ?: return ResponseEntity.notFound().build()
        val imageBytes = imageStoreService.getImage(imageName) ?: return ResponseEntity.notFound().build()

        val contentType = when {
            imageOrNull.name.endsWith(".jpg", true)
                    || imageOrNull.name.endsWith(".jpeg", true) -> MediaType.IMAGE_JPEG

            imageOrNull.name.endsWith(".png", true) -> MediaType.IMAGE_PNG
            imageOrNull.name.endsWith(".gif", true) -> MediaType.IMAGE_GIF
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        return ResponseEntity.ok().contentType(contentType)
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(imageBytes)
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

        return ResponseEntity
            .status(HttpStatus.SEE_OTHER)
            .location(URI.create("/api/v1/images/${savedImage.id}"))
            .build()
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