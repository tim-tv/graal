package com.github.titovart.graal.user.repository

import com.github.titovart.graal.user.model.User
import org.springframework.data.repository.PagingAndSortingRepository


interface UserRepository : PagingAndSortingRepository<User, Long> {

    fun findByNickName(nickName: String): User?
}