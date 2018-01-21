package com.github.titovart.graal.aggregation.entity

import java.sql.Timestamp


data class TokenResponse(val accessToken: String, val expiresIn: Long, val expiresAt: Timestamp)
