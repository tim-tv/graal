package com.github.titovart.graal.aggregation.entity.post

import java.util.*

data class PostFeignResponse(
        val id: Long,
        val content: String,
        val caption: String?,
        val userId: Long,
        val tags: MutableSet<Long>,
        val createdDateTime: Date,
        val lastModifiedDateTime: Date?
)