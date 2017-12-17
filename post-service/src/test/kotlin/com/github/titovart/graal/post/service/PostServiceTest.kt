package com.github.titovart.graal.post.service

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.repository.PostRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Date
import java.util.*
import javax.persistence.EntityNotFoundException
import kotlin.test.assertEquals


@RunWith(SpringRunner::class)
class PostServiceTest {

    private lateinit var postService: PostServiceImpl

    private lateinit var repository: PostRepository

    @Before
    fun setup() {
        repository = Mockito.mock(PostRepository::class.java)
        postService = PostServiceImpl(repository)
    }

    @Test
    fun shouldFindPostById() {
        val post = getFirstPost()

        Mockito.`when`(repository.findById(post.id)).thenReturn(Optional.of(post))

        val foundPost = postService.findById(post.id)

        assertEquals(post, foundPost)
    }

    @Test(expected = EntityNotFoundException::class)
    fun shouldThrowEntityNotFoundExceptionWhenFindPostWithInvalidId() {
        val post = getFirstPost()

        Mockito.`when`(repository.findById(anyLong())).thenReturn(Optional.empty())
        Mockito.`when`(repository.findById(post.id)).thenReturn(Optional.of(post))

        val foundPost = postService.findById(post.id)

        assertEquals(post, foundPost)

        postService.findById(359L)
    }

    @Test
    fun shouldFindAllPosts() {
        val posts = listOf(getFirstPost(), getSecondPost(), getThirdPost())
        Mockito.`when`(repository.findAll(any(Pageable::class.java))).thenReturn(PageImpl(posts))

        val foundPosts = postService.findAll(PageRequest.of(1, 10))

        assertEquals(posts, foundPosts.content)
    }

    @Test
    fun shouldFindByUserId() {

        Mockito.`when`(repository.findByUserId(anyLong(), safeAny(Pageable::class.java))).thenAnswer({
            val id = it.arguments[0] as Long
            if (id == 1L) {
                PageImpl(listOf(getFirstPost(), getSecondPost()))
            } else if (id == 2L) {
                PageImpl(listOf(getThirdPost()))
            } else {
                throw EntityNotFoundException()
            }
        })

        val foundPostByUserId1 = postService.findByUserId(1L, PageRequest.of(1, 10))
        assertEquals(foundPostByUserId1.content, listOf(getFirstPost(), getSecondPost()))

        val foundPostByUserId2 = postService.findByUserId(2L, PageRequest.of(1, 10))
        assertEquals(foundPostByUserId2.content, listOf(getThirdPost()))
    }

    @Test(expected = EntityNotFoundException::class)
    fun shouldThrowEntityNotFoundWhenFindPostsByInvalidUserId() {
        Mockito.`when`(repository.findByUserId(anyLong(), safeAny(Pageable::class.java))).thenAnswer({
            val id = it.arguments[0] as Long
            if (id == 1L) {
                PageImpl(listOf(getFirstPost(), getSecondPost()))
            } else if (id == 2L) {
                PageImpl(listOf(getThirdPost()))
            } else {
                throw EntityNotFoundException()
            }
        })

        val foundPostByUserId1 = postService.findByUserId(1L, PageRequest.of(1, 10))
        assertEquals(foundPostByUserId1.content, listOf(getFirstPost(), getSecondPost()))

        postService.findByUserId(4L, PageRequest.of(1, 10))
    }

    @Test
    fun shouldCreatePost() {
        Mockito.`when`(repository.save(any(Post::class.java))).thenAnswer({
            val p = it.arguments[0] as Post
            if (p.id == -1L) {
                Post(p.content, p.caption, p.userId, p.tags, Date(47), null, 4L)
            } else {
                p
            }
        })

        val postRequest = getFirstPost()
        val post = postService.save(postRequest)

        assertEquals(post.content, postRequest.content)
        assertEquals(post.caption, postRequest.caption)
        assertEquals(post.userId, postRequest.userId)
        assertEquals(post.tags, postRequest.tags)
    }

    private fun getFirstPost() = Post("ga", "ez", 1L, mutableSetOf(1L, 2L, 3L), Date(1), null, 1L)

    private fun getSecondPost() = Post("a", "b", 1L, mutableSetOf(1L, 4L), Date(1), null, 2L)

    private fun getThirdPost() = Post("c", "d", 2L, mutableSetOf(3L, 2L), Date(1), Date(2), 3L)

    private fun <T> safeAny(clazz: Class<T>): T = any(clazz) ?: null as T

}
