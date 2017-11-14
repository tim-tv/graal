package com.github.titovart.graal.post.model


import java.util.Date
import javax.persistence.*


@Entity
data class Post(

        var content: String,

        var caption: String?,

        var userId: Long,

        @ElementCollection
        @CollectionTable(name = "tags")
        var tags: Set<Long> = emptySet(),

        @Column(insertable = false, updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        @Temporal(TemporalType.TIMESTAMP)
        val createdDateTime: Date = Date(),

        @Column(insertable = false, updatable = true)
        @Temporal(TemporalType.TIMESTAMP)
        var lastModifiedDateTime: Date? = null,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1
)