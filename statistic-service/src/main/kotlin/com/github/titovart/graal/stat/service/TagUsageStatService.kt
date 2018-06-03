package com.github.titovart.graal.stat.service

import com.github.titovart.graal.stat.model.TagUsageStatistic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface TagUsageStatService {

    fun findById(id: Long): TagUsageStatistic

    fun findAll(pageable: Pageable): Page<TagUsageStatistic>

    fun findByTag(tag: String): TagUsageStatistic

    fun addTagAsUsed(hashTag: String): TagUsageStatistic

    fun findTopUsedTag(pageable: Pageable): Page<TagUsageStatistic>
}