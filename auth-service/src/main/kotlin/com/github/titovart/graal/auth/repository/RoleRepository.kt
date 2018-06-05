package com.github.titovart.graal.auth.repository

import com.github.titovart.graal.auth.domain.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface RoleRepository : CrudRepository<Role, Long> {

    fun findByName(roleName: String): Role?
}
