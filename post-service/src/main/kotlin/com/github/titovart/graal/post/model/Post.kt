package com.github.titovart.graal.post.model


import java.util.Date
import javax.persistence.*


@Entity
data class Post(

        @Column(length = CONTENT_LEN)
        var content: String,

        @Column(length = CAPTION_LEN)
        var caption: String?,

        var userId: Long,

        @ElementCollection
        @CollectionTable(name = "tags")
        var tags: MutableSet<Long> = mutableSetOf(),

        @Column(insertable = false, updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        @Temporal(TemporalType.TIMESTAMP)
        val createdDateTime: Date = Date(),

        @Column(insertable = false, updatable = true)
        @Temporal(TemporalType.TIMESTAMP)
        var lastModifiedDateTime: Date? = null,

        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1
) {
    companion object {
        /** The max value of content length for posts checked by the database. */
        const val CONTENT_LEN = 500

        /** The max value of caption length for posts checked by the database. */
        const val CAPTION_LEN = 200
    }
}
