package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.RoleRepository
import com.github.titovart.graal.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityExistsException


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Transactional
    override fun create(user: User): User {
        throwEntityNotFoundExceptionIfUserExists(user)

        val roles =
            user.getRoles().map {
                roleRepository.findByName(it.name) ?: roleRepository.save(it)
            }.toList()

        val passwordHash = passwordEncoder.encode(user.password)
        user.password = passwordHash
        user.setRoles(roles)

        return userRepository.save(user)
    }

    @Transactional
    override fun delete(user: User) {
        userRepository.delete(user)
    }

    private fun throwEntityNotFoundExceptionIfUserExists(user: User) {
        userRepository.findByUsername(user.username)
            ?.let { throw EntityExistsException("User @${user.username} already exists") }
        userRepository.findByEmail(user.username)
            ?.let {
                throw EntityExistsException("User with email ${user.getEmail()} already exists")
            }
    }

}