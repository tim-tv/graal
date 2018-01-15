package com.github.titovart.graal.aggregation.client

import com.github.titovart.graal.aggregation.entity.AuthResponse
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient(name = "auth-service")
interface AuthClient {

    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/me")
    fun me(@RequestHeader("Authorization") token: String): ResponseEntity<AuthResponse>
}
