package com.github.titovart.graal.aggregation.client

import com.github.titovart.graal.aggregation.entity.tag.Tag
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "hashtag-service", configuration = arrayOf(FeignClientContext::class))
interface TagClient {

    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/hashtags/{id}")
    fun getById(@PathVariable("id") id: Long): ResponseEntity<Tag>

    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/hashtags/search")
    fun getByValue(@RequestParam("value") value: String): ResponseEntity<Tag>

    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/hashtags")
    fun create(tagRequest: Tag): ResponseEntity<Unit>
}