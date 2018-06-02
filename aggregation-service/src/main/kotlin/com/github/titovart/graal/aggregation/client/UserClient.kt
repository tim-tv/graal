package com.github.titovart.graal.aggregation.client

import com.github.titovart.graal.aggregation.client.secure.SecureClient
import com.github.titovart.graal.aggregation.entity.user.UserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient(name = "user-service")
interface UserClient : SecureClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/users/{id}"])
    fun getById(@PathVariable("id") id: Long): ResponseEntity<UserResponse>
}
