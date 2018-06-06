package com.github.titovart.graal.auth.entity

data class UserRequest(
    val username: String,
    val email: String,
    val password: String
)