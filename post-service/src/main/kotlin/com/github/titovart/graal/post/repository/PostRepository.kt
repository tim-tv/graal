package com.github.titovart.graal.post.repository

import com.github.titovart.graal.post.model.Post
import org.springframework.data.repository.PagingAndSortingRepository


interface PostRepository : PagingAndSortingRepository<Post, Long>