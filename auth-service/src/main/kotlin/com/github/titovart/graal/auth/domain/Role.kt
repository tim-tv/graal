package com.github.titovart.graal.auth.domain

import javax.persistence.*


@Entity
data class Role(

    @Column(nullable = false)
    val name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1L
)