package com.github.titovart.graal.stat.web

import com.github.titovart.graal.stat.model.TagUsageStatistic
import com.github.titovart.graal.stat.service.TagUsageStatService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping
class StatisticController(private val service: TagUsageStatService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/tags/top")
    fun findTopUsedTags(pageable: Pageable): Page<TagUsageStatistic> {
        return service.findAll(pageable).also { page ->
            logger.info("[findTopUsedTags($pageable)] => $page")
        }
    }

}
