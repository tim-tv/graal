package com.github.titovart.graal.user.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


@Entity
data class User(
        @Column(nullable = false, unique = true)
        @field:Size(min = 1, max = 30, message = "Username size must be in range [1, 30].")
        @field:Pattern(regexp = "^\\w+\$", message = "Invalid username.")
        var nickName: String,

        @Column(nullable = false, unique = true)
        var email: String,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1
)