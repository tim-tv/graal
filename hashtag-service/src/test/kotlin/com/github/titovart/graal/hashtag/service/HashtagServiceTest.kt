package com.github.titovart.graal.hashtag.service

import com.github.titovart.graal.hashtag.model.HashTag
import com.github.titovart.graal.hashtag.repository.HashTagRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
class HashtagServiceTest {

    private lateinit var tagService: HashTagServiceImpl

    private lateinit var repository: HashTagRepository

    @Before
    fun setup() {
        repository = Mockito.mock(HashTagRepository::class.java)
        tagService = HashTagServiceImpl(repository = repository)

        val tag1 = getFirstTag()
        Mockito.`when`(repository.findByValue(anyString())).thenReturn(null)
        Mockito.`when`(repository.findByValue(tag1.value)).thenReturn(tag1)

        Mockito.`when`(repository.findById(anyLong())).thenReturn(Optional.empty())
        Mockito.`when`(repository.findById(tag1.id)).thenReturn(Optional.of(tag1))

        Mockito.`when`(repository.save(ArgumentMatchers.any(HashTag::class.java))).thenAnswer({
            val tag = it.arguments[0] as HashTag
            if (tag.id == -1L) {
                HashTag(tag.value, 4L)
            } else {
                tag
            }
        })
    }

    @Test
    fun shouldFindByValue() {
        val tag = getFirstTag()
        val foundTag = tagService.findByValue(tag.value)
        assertEquals(foundTag, tag)
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwEntityNotFoundExceptionWhenFindWithInvalidValue() {
        tagService.findByValue("1234")
    }

    @Test
    fun shouldFindById() {
        val tag = getFirstTag()
        val foundTag = tagService.findById(tag.id)
        assertEquals(foundTag, tag)
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwEntityNotFoundExceptionWhenFindWithInvalidId() {
        tagService.findById(123L)
    }

    @Test
    fun shouldFindAll() {

        val allTags = listOf(getFirstTag(), getSecondTag(), getThirdTag())

        Mockito.`when`(repository.findAll(ArgumentMatchers.any(Pageable::class.java)))
                .thenReturn(PageImpl(allTags))

        val page = tagService.findAll(PageRequest.of(1, 10))

        assertEquals(page.content, allTags)
    }

    @Test
    fun shouldCreateTag() {
        val tag = HashTag("besttag")
        val createdTag = tagService.save(tag)

        assertEquals(createdTag.value, tag.value)
        assertEquals(createdTag.id, 4L)
    }

    @Test(expected = EntityExistsException::class)
    fun shouldThrowEntityExistsExceptionWhenCreateTheSameTag() {
        val tag = getFirstTag()

        tagService.save(tag)
    }

    @Test
    fun shouldDeleteTagById() {
        Mockito.`when`(repository.deleteById(1L)).then { it }

        tagService.delete(1L)
    }

    @Test(expected = NonTransientDataAccessException::class)
    fun throwExceptionWhenDeleteByInvalidId() {
        Mockito.`when`(repository.deleteById(anyLong())).thenAnswer({
            val id = it.arguments[0] as Long
            if (id != 1L) {
                throw EmptyResultDataAccessException(1)
            }
        })

        tagService.delete(1L)
        tagService.delete(123L)
    }


    private fun getFirstTag() = HashTag("like4like", 1L)

    private fun getSecondTag() = HashTag("bestmother", 2L)

    private fun getThirdTag() = HashTag("wedding", 3L)
}