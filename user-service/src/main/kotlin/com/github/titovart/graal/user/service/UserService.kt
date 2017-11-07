package com.github.titovart.graal.user.service

import com.github.titovart.graal.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface UserService {

    fun findById(userId: Long): User

    fun findAll(pageable: Pageable): Page<User>

    fun findByUserName(username: String): User

    fun save(user: User): User

    fun update(userId: Long, user: User): User

    fun delete(userId: Long)
}