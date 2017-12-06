package com.github.titovart.graal.aggregation.entity.post

data class PostFeignRequest(
    val content: String,
    val caption: String = "",
    val userId: Long,
    val tags: Set<Long> = emptySet()
)
