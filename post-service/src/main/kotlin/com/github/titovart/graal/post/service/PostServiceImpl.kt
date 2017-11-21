package com.github.titovart.graal.post.service

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.model.PostRequest
import com.github.titovart.graal.post.repository.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import javax.persistence.EntityNotFoundException

@Service
class PostServiceImpl(private val repository: PostRepository) : PostService {

    override fun findByUserId(userId: Long, pageable: Pageable): Page<Post> {
        return repository.findByUserId(userId, pageable)
    }

    @Transactional(readOnly = true)
    override fun findById(postId: Long): Post {
        val post = repository.findById(postId)
        return post.orElseThrow { throw EntityNotFoundException("Post(id=$postId) not found") }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Post> {
        return repository.findAll(pageable)
    }

    @Transactional
    override fun save(post: Post): Post {
        return repository.save(post)
    }

    @Transactional
    override fun update(postId: Long, post: PostRequest): Post {
        val oldPost = repository.findById(postId).orElseThrow {
            throw EntityNotFoundException("Post(id=$postId) not found")
        }

        post.caption?.let { caption -> oldPost.caption = caption }
        post.content?.let { content -> oldPost.content = content }
        post.tags?.let { tags -> oldPost.tags = tags.toMutableSet() }
        oldPost.lastModifiedDateTime = Date()

        return repository.save(oldPost)
    }

    @Transactional
    override fun delete(postId: Long) {
        return repository.deleteById(postId)
    }
}