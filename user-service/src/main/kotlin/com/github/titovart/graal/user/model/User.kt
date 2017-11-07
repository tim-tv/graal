package com.github.titovart.graal.user.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType



@Entity
data class User(
        @Column(nullable = false, unique = true)
        var nickName: String,

        @Column(nullable = false, unique = true)
        var email: String,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1
)