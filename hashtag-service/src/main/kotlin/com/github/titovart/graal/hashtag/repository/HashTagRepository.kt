package com.github.titovart.graal.hashtag.repository

import com.github.titovart.graal.hashtag.model.HashTag
import org.springframework.data.repository.PagingAndSortingRepository


interface HashTagRepository : PagingAndSortingRepository<HashTag, Long> {

    fun findByValue(value: String): HashTag?
}