package com.pru.lib

import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

fun getLongUrlFromShortUrl(shortUrl: String): String {
    val httpClient = HttpClients.createDefault()
    val httpHead = HttpHead(shortUrl)

    try {
        val response = httpClient.execute(httpHead)
        val locationHeader = response.getFirstHeader("Location")
        val longUrl = locationHeader?.value ?: "Long URL not found"

        // Close resources
        EntityUtils.consume(response.entity)
        response.close()

        return longUrl
    } catch (e: Exception) {
        e.printStackTrace()
        return "Error fetching long URL"
    }
}



fun main() {
    // Example usage
    val shortUrl = "https://klr.pw/sPcJwe"
    val longUrl = getLongUrlFromShortUrl(shortUrl)
    println("Long URL: $longUrl")
}