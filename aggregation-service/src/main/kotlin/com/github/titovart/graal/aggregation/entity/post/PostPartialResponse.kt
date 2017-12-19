package com.github.titovart.graal.aggregation.entity.post

import com.github.titovart.graal.aggregation.entity.user.UserResponse
import java.util.Date

data class PostPartialResponse(
        val id: Long,
        val content: String,
        val caption: String?,
        val user: UserResponse,
        val tagsRef: String,
        val createdDateTime: Date,
        val lastModifiedDateTime: Date?
) {
    companion object {
        fun create(feignPost: PostFeignResponse, user: UserResponse, tagsRef: String) =
                PostPartialResponse(feignPost.id, feignPost.content, feignPost.caption, user,
                        tagsRef, feignPost.createdDateTime, feignPost.lastModifiedDateTime)
    }
}