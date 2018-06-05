package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(private val repository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user =
            repository.findByUsername(username!!) ?: throw UsernameNotFoundException(username)

        val roles = user.getRoles().map { SimpleGrantedAuthority(it.name) }.toMutableList()

        return User(user.username, user.password, roles)
    }

}