package com.github.titovart.graal.aggregation.entity.post

import com.github.titovart.graal.aggregation.entity.user.UserResponse
import java.util.Date


data class PostResponse(
        val id: Long,
        val content: String,
        val caption: String?,
        val user: UserResponse,
        val tags: MutableSet<String>,
        val createdDateTime: Date,
        val lastModifiedDateTime: Date?
) {
    companion object {
        fun fromFeign(feignPost: PostFeignResponse, tags: MutableSet<String>, user: UserResponse) =
                PostResponse(feignPost.id, feignPost.content, feignPost.caption, user,
                        tags, feignPost.createdDateTime, feignPost.lastModifiedDateTime)
    }
}