package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun create(user: User) {
        val passwordHash = passwordEncoder.encode(user.password)
        user.password = passwordHash

        repository.save(user)
    }

}