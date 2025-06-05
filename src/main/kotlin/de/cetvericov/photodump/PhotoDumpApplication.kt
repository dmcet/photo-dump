package de.cetvericov.photodump

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PhotoDumpApplication

fun main(args: Array<String>) {
    runApplication<PhotoDumpApplication>(*args)
}
