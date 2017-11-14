package com.github.titovart.graal.post.model

data class PostRequest(
        val caption: String? = null,
        val content: String? = null,
        val tags: Set<Long>? = emptySet()
)
