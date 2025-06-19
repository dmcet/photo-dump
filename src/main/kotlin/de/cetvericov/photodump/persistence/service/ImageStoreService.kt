package de.cetvericov.photodump.persistence.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import kotlin.io.path.*

interface ImageStoreService {
    suspend fun saveImage(filename: String, image: ByteArray): String?
    suspend fun getImage(filename: String): ByteArray?
    suspend fun deleteImage(filename: String)
}

@Service
class FileSystemImageStoreService(
    @Value("\${app.storage.location}")
    basePath: String
) : ImageStoreService {
    private val baseFile = File(basePath)
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("Base path for image storage: $basePath")
        if (!baseFile.exists() && !baseFile.mkdirs()) {
            logger.error("Failed to create base directory for image storage")
        }
        logger.info("Base directory for image storage ready")
    }

    override suspend fun saveImage(filename: String, image: ByteArray): String? = withContext(Dispatchers.IO) {
        logger.info("Attempting to save $filename")

        return@withContext try {
            val filePath = Path(baseFile.path, filename)

            if (filePath.exists()) {
                logger.warn("File $filename already exists, will not be overwritten")
                null
            }

            filePath.writeBytes(image)

            logger.info("Successfully saved $filename")
            filename
        } catch (e: Exception) {
            logger.error("Failed to save $filename", e)
            null
        }
    }

    override suspend fun getImage(filename: String): ByteArray? = withContext(Dispatchers.IO) {
        logger.info("Request for image $filename was made, trying to serve it")

        return@withContext try {
            val imagePath = Path(baseFile.path, filename)

            if (!imagePath.exists()) {
                logger.error("Image $filename does not exist")
                null
            }

            imagePath.readBytes()
        } catch (e: Exception) {
            logger.error("Failed to get $filename", e)
            null
        }
    }

    override suspend fun deleteImage(filename: String) = withContext(Dispatchers.IO) {
        logger.info("Attempting to delete $filename")

        try {
            val imagePath = Path(baseFile.path, filename)

            if (!imagePath.exists()) {
                logger.warn("Image $filename does not exist, nothing to delete")
                return@withContext
            }

            imagePath.deleteExisting()
            logger.info("Successfully deleted $filename")

        } catch (e: Exception) {
            logger.error("Failed to delete $filename", e)
        }
    }
}