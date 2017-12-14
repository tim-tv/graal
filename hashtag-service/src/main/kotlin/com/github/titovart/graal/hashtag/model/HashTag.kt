package com.github.titovart.graal.hashtag.model

import javax.persistence.*


@Entity
data class HashTag(

        @Column(nullable = false, unique = true, length = 200)
        var value: String,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1
)
