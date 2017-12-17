package com.github.titovart.graal.hashtag.model

import javax.persistence.*
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


@Entity
data class HashTag(

        @Column(nullable = false, unique = true, length = 200)
        @field:Size(min=1, max = 200, message = "Hashtag value size must be in range [1, 200].")
        @field:Pattern(regexp = "^\\w+\$", message = "Invalid hashtag value.")
        var value: String,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1
)
