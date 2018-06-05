package com.github.titovart.graal.auth.service

import com.github.titovart.graal.auth.domain.Role

interface RoleService {

    fun createRole(role: Role)
}
