package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.Role
import com.github.titovart.graal.auth.repository.RoleRepository
import org.springframework.stereotype.Service


@Service
class RoleServiceImpl(private val repository: RoleRepository) : RoleService {

    override fun createRole(role: Role) {
        repository.save(role)
    }
}