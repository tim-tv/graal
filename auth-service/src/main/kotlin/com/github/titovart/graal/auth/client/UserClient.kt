package com.github.titovart.graal.auth.client

import com.github.titovart.graal.auth.entity.UserFeignRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient(name = "user-service")
interface UserClient {

    @RequestMapping(method = [RequestMethod.POST], value = ["/users"])
    fun create(user: UserFeignRequest): ResponseEntity<Unit>
}
