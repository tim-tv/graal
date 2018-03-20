package com.github.titovart.spring.service.secure.server.entity

import java.sql.Timestamp

data class AccessTokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val expiresAt: Timestamp
)
