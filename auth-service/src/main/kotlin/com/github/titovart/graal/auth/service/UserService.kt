package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.User

interface UserService {

    fun create(user: User)
}
