package com.github.titovart.graal.hashtag.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.titovart.graal.hashtag.model.HashTag
import com.github.titovart.graal.hashtag.service.HashTagService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations.initMocks
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestContext
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.util.NestedServletException
import javax.persistence.EntityNotFoundException


@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = arrayOf(TestContext::class))
class HashtagControllerTest {

    private val mapper = ObjectMapper()

    @InjectMocks
    private lateinit var tagController: HashTagController

    @Mock
    private lateinit var tagService: HashTagService

    private lateinit var mockMvc: MockMvc

    @Before
    fun setup() {
        initMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build()
    }

    @Test
    fun shouldFindTagByValue() {
        val tag = createTag()

        Mockito.`when`(tagService.findByValue(tag.value)).thenReturn(tag)

        val actions = mockMvc.perform(get("/hashtags/find?value=${tag.value}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        assertEqualsTag(actions, tag)
    }

    @Test(expected = EntityNotFoundException::class)
    fun shouldThrowEntityNotFoundExceptionWhenFindTagByInvalidValue() {
        val tag = createTag()

        Mockito.`when`(tagService.findByValue(anyString())).thenThrow(EntityNotFoundException())
        Mockito.`when`(tagService.findByValue(tag.value)).thenReturn(tag)

        val actions = mockMvc.perform(get("/hashtags/find?value=${tag.value}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        assertEqualsTag(actions, tag)

        mockMvc.perform(get("/hashtags/find?value=cat")).andExpect(status().isNotFound)
    }

    @Test
    fun shouldFindTagById() {
        val tag = createTag()

        Mockito.`when`(tagService.findById(tag.id)).thenReturn(tag)

        val actions = mockMvc.perform(get("/hashtags/{id}", tag.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        assertEqualsTag(actions, tag)
    }

    @Test(expected = EntityNotFoundException::class)
    fun shouldThrowEntityNotFoundExceptionWhenFindTagByInvalidId() {
        val tag = createTag()

        Mockito.`when`(tagService.findById(anyLong())).thenThrow(EntityNotFoundException())
        Mockito.`when`(tagService.findById(tag.id)).thenReturn(tag)

        val actions = mockMvc.perform(get("/hashtags/{id}", tag.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        assertEqualsTag(actions, tag)

        mockMvc.perform(get("/hashtags/{id}", 123L)).andExpect(status().isNotFound)
    }

    @Test
    fun shouldCreateTag() {
        Mockito.`when`(tagService.save(safeAny())).thenAnswer({
            val tag = it.arguments[0] as HashTag
            HashTag(tag.value, 4L)
        })

        val newTag = HashTag("catsarelife")
        val json = mapper.writeValueAsString(newTag)

        mockMvc.perform(
            post("/hashtags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("location", "/hashtags/4"))
    }

    @Test
    fun shouldDeleteTag() {
        Mockito.`when`(tagService.delete(anyLong())).then({ it })

        val tag = createTag()

        mockMvc.perform(delete("/hashtags/{id}", tag.id))
            .andExpect(status().isNoContent)
    }

    @Test(expected = NestedServletException::class)
    fun shouldThrowEntityNotFoundExceptionWhenDeleteNotExistedTag() {
        Mockito.`when`(tagService.delete(anyLong())).thenAnswer({
            val id = it.arguments[0] as Long
            if (id != 1L) {
                throw EntityNotFoundException()
            }
        })

        val tag = createTag()

        mockMvc.perform(delete("/hashtags/{id}", tag.id))
            .andExpect(status().isNoContent)

        mockMvc.perform(delete("/hashtags/{id}", 123L))
            .andExpect(status().isNoContent)
    }

    private fun createTag() = HashTag("happymom", 1L)

    private fun assertEqualsTag(actions: ResultActions, tag: HashTag) {
        actions.andExpect(jsonPath("$.id").value(tag.id))
            .andExpect(jsonPath("$.value").value(tag.value))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> safeAny(): T = any() ?: null as T
}
