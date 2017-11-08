package com.github.titovart.graal.hashtag.service

import com.github.titovart.graal.hashtag.model.HashTag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface HashTagService {

    fun findById(id: Long): HashTag

    fun findAll(pageable: Pageable): Page<HashTag>

    fun findByValue(value: String): HashTag

    fun save(hashTag: HashTag): HashTag

    fun delete(id: Long)
}