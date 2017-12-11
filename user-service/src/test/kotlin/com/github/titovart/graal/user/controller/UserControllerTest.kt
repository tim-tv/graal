package com.github.titovart.graal.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.titovart.graal.user.model.User
import com.github.titovart.graal.user.service.UserService
import com.github.titovart.graal.user.web.UserController
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
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
class UserControllerTest {

    private val mapper = ObjectMapper()

    @InjectMocks
    lateinit var userController: UserController

    @Mock
    lateinit var userService: UserService

    lateinit var mockMvc: MockMvc


    @Before
    fun setup() {
        initMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
    }

    @Test
    fun shouldFindUserById() {
        val user = User("jane", "jane@bmstu.ru", id = 1L)

        Mockito.`when`(userService.findById(1L)).thenReturn(user)

        val actions = mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        buildCheck(actions, user)
    }


    @Test(expected = NestedServletException::class)
    fun throwExceptionWhenUserWithGivenIdNotFound() {
        Mockito.`when`(userService.findById(anyLong())).thenThrow(EntityNotFoundException())
        mockMvc.perform(get("/users/{id}", 122L))
    }

    @Test
    fun shouldFindUserByName() {
        val user = User("jane", "jane@bmstu.ru", id = 1L)

        Mockito.`when`(userService.findByUserName("jane")).thenReturn(user)

        val actions = mockMvc.perform(get("/users/find?username=jane"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        buildCheck(actions, user)
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwExceptionWhenUserWithGiveNameNotFound() {
        val user = User("jane", "jane@bmstu.ru", id = 1L)
        Mockito.`when`(userService.findByUserName(anyString())).thenThrow(EntityNotFoundException())
        Mockito.`when`(userService.findByUserName("jane")).thenReturn(user)

        val actions = mockMvc.perform(get("/users/find?username=jane"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

        buildCheck(actions, user)

        mockMvc.perform(get("/users/find?username=jane123"))
    }

    @Test
    fun shouldCreateNewUser() {
        val user = User("jane", "jane@bmstu.ru", id = 1L)
        val json = mapper.writeValueAsString(user)


        Mockito.`when`(userService.save(user)).thenReturn(user)

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated)
                .andExpect(header().string("location", "/users/1"))
    }

    @Test
    fun shouldUpdateNewUser() {
        val user = User("jane", "jane@bmstu.ru", id = 1L)
        val json = mapper.writeValueAsString(user)


        Mockito.`when`(userService.update(1L, user)).thenAnswer({
            val id = it.arguments[0] as Long
            val usr = it.arguments[1] as User

            if (id == 1L) {
                User(usr.nickName, usr.email, 1L)
            } else {
                throw EntityNotFoundException()
            }
        })

        val actions = mockMvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk)

        buildCheck(actions, user)
    }

    private fun buildCheck(actions: ResultActions, user: User) {
        actions.andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nickName").value(user.nickName))
                .andExpect(jsonPath("$.email").value(user.email))
    }
}