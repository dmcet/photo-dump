package de.cetvericov.photodump.images.config

import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ImageTranscodingConfiguration {
    @Bean
    fun imageTranscoder(): ImageTranscoder = PNGTranscoder()
}