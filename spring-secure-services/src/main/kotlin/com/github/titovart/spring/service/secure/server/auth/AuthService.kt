package com.github.titovart.spring.service.secure.server.auth

import com.github.titovart.spring.service.secure.server.details.ServiceDetails
import com.github.titovart.spring.service.secure.server.token.AccessToken


interface AuthService {

    fun getOrGenerateToken(serviceDetails: ServiceDetails): AccessToken

    fun validateToken(tokenValue: String): Boolean
}
