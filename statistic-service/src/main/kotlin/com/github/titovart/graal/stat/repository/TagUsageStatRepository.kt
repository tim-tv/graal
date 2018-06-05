package com.github.titovart.graal.stat.repository

import com.github.titovart.graal.stat.domain.TagUsageStatistic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository


interface TagUsageStatRepository : PagingAndSortingRepository<TagUsageStatistic, Long> {

    fun findByTag(tag: String): TagUsageStatistic?

    fun findAllByOrderByCountDesc(pageable: Pageable): Page<TagUsageStatistic>
}