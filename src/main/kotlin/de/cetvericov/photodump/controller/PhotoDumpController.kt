package de.cetvericov.photodump.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PhotoDumpController {
    @GetMapping("/upload")
    fun greeting(
    ): String = "redirect:/upload.html"
}