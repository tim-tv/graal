package com.github.titovart.graal.user.service

import com.github.titovart.graal.user.model.User
import com.github.titovart.graal.user.repository.UserRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import javax.persistence.EntityNotFoundException
import kotlin.test.assertEquals


@RunWith(SpringRunner::class)
class UserServiceTest {

    private lateinit var userService: UserServiceImpl

    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        repository = Mockito.mock(UserRepository::class.java)
        userService = UserServiceImpl(repository = repository)

        val user = getFirstUser()

        Mockito.`when`(repository.findByNickName(anyString())).thenReturn(null)
        Mockito.`when`(repository.findByNickName(user.nickName)).thenReturn(user)

        Mockito.`when`(repository.findById(anyLong())).thenReturn(Optional.empty())
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(user))

        Mockito.`when`(repository.findAll(any(Pageable::class.java)))
            .thenReturn(PageImpl(getAllUsers()))

        Mockito.`when`(repository.save(any(User::class.java))).thenAnswer({
            val usr = it.arguments[0] as User
            if (usr.id == -1L) {
                User(usr.nickName, usr.email, 2L)
            } else {
                usr
            }

        })
    }

    @Test
    fun shouldFindByName() {
        val user = userService.findByUserName("jane")
        assertEquals(user, getFirstUser())
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwEntityNotFoundExceptionWhenNoUserExistedWithTheGivenName() {
        userService.findByUserName("jane123")
    }

    @Test
    fun shouldFindById() {
        val user = userService.findById(1L)
        assertEquals(user, getFirstUser())
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwEntityNotFoundExceptionWhenNoUserExistedWithTheGivenId() {
        userService.findById(12L)
    }

    @Test
    fun shouldFindAll() {
        val page = userService.findAll(PageRequest.of(1, 10))

        assertEquals(page.content, getAllUsers())
    }

    @Test
    fun shouldCreateNewUser() {
        val newUser = User("mr.robot", "mr.robot@fsociaty.com")
        val createdUser = userService.save(newUser)

        assertEquals(createdUser.nickName, newUser.nickName)
        assertEquals(createdUser.email, newUser.email)
        assertEquals(createdUser.id, 2L)
    }

    @Test(expected = EntityNotFoundException::class)
    fun throwEntityNotFoundExceptionWhenInvalidIdWhileUpdateUser() {
        val user = getThirdUser()

        userService.update(3L, user)
    }

    @Test
    fun shouldUpdateUser() {
        val firstUser = getFirstUser()
        val newUser = User(firstUser.nickName, "newmail@mail.com")


        val createdUser = userService.update(1L, newUser)
        assertEquals(newUser.nickName, createdUser.nickName)
        assertEquals(newUser.email, createdUser.email)
        assertEquals(createdUser.id, 1L)
    }

    private fun getFirstUser() = User("jane", "jane@email.org", 1L)

    private fun getSecondUser() = User("mr.president", "putin@motherrussia.ru", 2L)

    private fun getThirdUser() = User("delphi_guru", "borisov@bmstu.ru", 3L)

    private fun getAllUsers() = listOf(getFirstUser(), getSecondUser(), getThirdUser())
}
