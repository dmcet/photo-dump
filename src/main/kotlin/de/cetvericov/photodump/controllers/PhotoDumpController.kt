package de.cetvericov.photodump.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PhotoDumpController {

    @GetMapping("/")
    fun index() = "Welcome to the PhotoDump application!"
}