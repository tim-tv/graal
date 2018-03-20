package com.github.titovart.graal.post.model


import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size


@Entity
data class Post(

    @Column(length = 500)
    @field:NotNull
    @field:Size(min = 1, max = 500, message = "Content size must be in range [1, 500].")
    var content: String,

    @Column(length = 200)
    @field:Size(min = 1, max = 200, message = "Caption size bust be in range [1, 200].")
    var caption: String?,

    @field:NotNull
    @field:Positive(message = "User's id must be positive.")
    val userId: Long,

    @ElementCollection
    @CollectionTable(name = "tags")
    @field:Size(max = 50, message = "Number of hashtags must be not more than 50.")
    var tags: MutableSet<Long> = mutableSetOf(),

    @Column(
        insertable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    @Temporal(TemporalType.TIMESTAMP)
    val createdDateTime: Date = Date(),

    @Column(insertable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    var lastModifiedDateTime: Date? = null,

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = -1
)
