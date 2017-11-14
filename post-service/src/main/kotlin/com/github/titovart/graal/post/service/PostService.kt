package com.github.titovart.graal.post.service

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.model.PostRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface PostService {

    fun findById(postId: Long): Post

    fun findAll(pageable: Pageable): Page<Post>

    fun save(post: Post): Post

    fun update(postId: Long, post: PostRequest): Post

    fun delete(postId: Long)
}
