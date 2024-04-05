package com.pru.geoshape

import com.pru.geoshape.Main.deleteRecursive
import com.pru.geoshape.model.PolygonPoint
import org.geotools.feature.SchemaException
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream
import java.io.IOException

@RestController
@RequestMapping("api/v1")
class MainController {

    @GetMapping("message")
    fun message(): String = "Hello there"

    @RequestMapping(path = ["/downloadShapeFile"], method = [RequestMethod.POST])
    @Throws(IOException::class, SchemaException::class, NoClassDefFoundError::class)
    fun download(@RequestBody polyList : List<PolygonPoint>): ResponseEntity<Resource> {
        val file = Main.prepareShapeFile(polyList)
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        val fileInputStream = FileInputStream(file)
        val resource = InputStreamResource(fileInputStream)
        val length = file.length()
        file.deleteRecursively()
        return ResponseEntity.ok().headers(header).contentLength(length)
            .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource)
    }
}