package com.pru.geoshape1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class MainController {

    @GetMapping("message")
    fun message(): String = "Hello there"
}