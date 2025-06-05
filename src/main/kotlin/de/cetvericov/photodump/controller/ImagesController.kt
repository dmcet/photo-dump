package de.cetvericov.photodump.controller

import de.cetvericov.photodump.model.Image
import de.cetvericov.photodump.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/images")
class ImagesController(private val imageRepository: ImageRepository) {

    @GetMapping
    fun getImages(): Flow<Image> = imageRepository.findAll().asFlow()


    @GetMapping("/{id}")
    suspend fun getImage(@PathVariable id: Long): Image? = imageRepository.findById(id).awaitFirstOrNull()

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadImage(@RequestPart("file") filePart: FilePart): Image {
        val bytes = filePart.content().map { buffer ->
            val bytes = ByteArray(buffer.readableByteCount())
            buffer.read(bytes)
            bytes
        }.reduce { acc, bytes -> acc + bytes }.awaitSingle()

        val image = Image(
            name = filePart.filename(),
            data = bytes
        )

        return imageRepository.save(image).awaitSingle()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteImage(@PathVariable id: Long) = imageRepository.deleteById(id).awaitFirstOrNull()
}