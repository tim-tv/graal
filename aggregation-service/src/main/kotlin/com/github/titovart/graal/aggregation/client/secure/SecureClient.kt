package com.github.titovart.graal.aggregation.client.secure

import com.github.titovart.graal.aggregation.entity.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


interface SecureClient {

    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/token")
    fun getToken(@RequestHeader("Authorization") token: String): ResponseEntity<TokenResponse>
}