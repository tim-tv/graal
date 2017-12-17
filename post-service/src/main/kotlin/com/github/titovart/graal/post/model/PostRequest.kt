package com.github.titovart.graal.post.model

import javax.validation.constraints.Size

data class PostRequest(

        @field:Size(min = 1, max = 200, message = "Caption size bust be in range [1, 200].")
        val caption: String? = null,

        @field:Size(min = 1, max = 500, message = "Content size must be in range [1, 500].")
        val content: String? = null,

        @field:Size(max = 50, message = "Number of hashtags must be not more than 50.")
        val tags: Set<Long>? = null
)
