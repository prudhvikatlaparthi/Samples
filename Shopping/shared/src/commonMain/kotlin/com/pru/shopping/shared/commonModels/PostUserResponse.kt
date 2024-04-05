package com.pru.shopping.shared.commonModels

import kotlinx.serialization.Serializable

@Serializable
data class PostUserResponse(
    val code: Int,
    val data: Data,
    val meta: Meta? = null
)

@Serializable
data class Meta(
    val pagination: Pagination? = null
)

@Serializable
data class Pagination(
    val limit: Int? = null,
    val page: Int? = null,
    val pages: Int? = null,
    val total: Int? = null
)
