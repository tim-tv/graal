package com.github.titovart.graal.aggregation.entity.post


data class PostRequest(
    val content: String,
    val caption: String = "",
    val tags: Set<String> = emptySet()
)