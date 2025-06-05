package de.cetvericov.photodump.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PhotoDumpController {
    @GetMapping("/greeting")
    fun greeting(
        @RequestParam(value = "name", required = false, defaultValue = "World") name: String,
        model: Model
    ): String {
        model.addAttribute("name", name)
        return "greeting"
    }
}