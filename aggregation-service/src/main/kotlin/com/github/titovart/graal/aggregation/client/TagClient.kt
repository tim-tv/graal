package com.github.titovart.graal.aggregation.client

import com.github.titovart.graal.aggregation.client.secure.SecureClient
import com.github.titovart.graal.aggregation.entity.tag.Tag
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "hashtag-service")
interface TagClient : SecureClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/hashtags/{id}"])
    fun getById(@PathVariable("id") id: Long): ResponseEntity<Tag>

    @RequestMapping(method = [RequestMethod.GET], value = ["/hashtags/find"])
    fun getByValue(@RequestParam("value") value: String): ResponseEntity<Tag>

    @RequestMapping(method = [RequestMethod.POST], value = ["/hashtags"])
    fun create(tagRequest: Tag): ResponseEntity<Unit>

    @RequestMapping(method = [RequestMethod.DELETE], value = ["/hashtags/{id}"])
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Unit>

}
