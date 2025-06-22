package de.cetvericov.photodump.images.service

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class ImagePreprocessingService(private val transcoder: ImageTranscoder) {
    fun transcode(input: ByteArray): ByteArray {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            transcoder.transcode(TranscoderInput(input.inputStream()), TranscoderOutput(byteArrayOutputStream))
            return byteArrayOutputStream.toByteArray()
        }
    }
}