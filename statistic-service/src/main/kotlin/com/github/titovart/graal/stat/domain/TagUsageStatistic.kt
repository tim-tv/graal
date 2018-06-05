package com.github.titovart.graal.stat.domain

import javax.persistence.*


@Entity
data class TagUsageStatistic(

    @Column(nullable = false, unique = true, length = 200)
    var tag: String,

    @Column(nullable = false)
    var count: Long,

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1
)
