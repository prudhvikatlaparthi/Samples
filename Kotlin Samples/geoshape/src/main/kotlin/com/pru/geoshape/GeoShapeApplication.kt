package com.pru.geoshape

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.SpringServletContainerInitializer
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class GeoShapeApplication : SpringBootServletInitializer(){

	override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
		return builder.sources(GeoShapeApplication::class.java)
	}
}

fun main(args: Array<String>) {
	runApplication<GeoShapeApplication>(*args)
}
