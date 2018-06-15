package com.github.titovart.graal.auth.client.secure

import com.github.titovart.graal.auth.entity.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


interface SecureClient {

    @RequestMapping(method = [RequestMethod.POST], value = ["/token"])
    fun getToken(@RequestHeader("Authorization") token: String): ResponseEntity<TokenResponse>
}