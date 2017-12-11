package com.github.titovart.graal.aggregation.entity

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap


class MessageResponseEntity<T>(
        status: HttpStatus,
        body: T? = null,
        headers: MultiValueMap<String, String>? = null,
        val message: String? = null) : ResponseEntity<T>(body, headers, status) {

    companion object {
        fun <T> fromResp(resp: ResponseEntity<T>): MessageResponseEntity<T> {
            return MessageResponseEntity(resp.statusCode, resp.body, resp.headers)
        }
    }

}
