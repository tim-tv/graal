package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(private val repository: UserRepository) : UserService {

    private val encoder = BCryptPasswordEncoder()

    override fun create(user: User) {
        val passwordHash = encoder.encode(user.password)
        user.password = passwordHash

        repository.save(user)
    }

}