package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class ShortUrl {
    var status: String? = null
    var message: String? = null
    private var token: String? = null
    var id: Long = 0
    @SerializedName("txtly")
    var shortUrl: String? = null

    override fun toString(): String {
        return "ShortUrl{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", id=" + id +
                ", shortUrl='" + shortUrl + '\'' +
                '}'
    }
}