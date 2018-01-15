package com.github.titovart.graal.aggregation.entity

data class AuthResponse(val oauth2Request: OAuth2Request, val principal: Principal)

data class OAuth2Request(val scope: List<String>)

data class Principal(val id: Long, val username: String)
