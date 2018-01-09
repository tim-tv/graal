package com.github.titovart.graal.post.repository

import com.github.titovart.graal.post.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository


interface PostRepository : PagingAndSortingRepository<Post, Long> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<Post>

    fun findByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Page<Post>
}