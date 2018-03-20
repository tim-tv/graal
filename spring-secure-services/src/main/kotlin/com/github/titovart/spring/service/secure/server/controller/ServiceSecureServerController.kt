package com.github.titovart.spring.service.secure.server.controller

import com.github.titovart.spring.service.secure.server.auth.AuthService
import com.github.titovart.spring.service.secure.server.details.ServiceDetails
import com.github.titovart.spring.service.secure.server.details.ServiceDetailsDto
import com.github.titovart.spring.service.secure.server.entity.AccessTokenResponse
import com.github.titovart.spring.service.secure.server.exception.AuthException
import org.apache.commons.codec.binary.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.util.*


@RestController
class ServiceSecureServerController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authService: AuthService


    @PostMapping("/token")
    fun generateToken(@RequestHeader headers: HttpHeaders): ResponseEntity<AccessTokenResponse> {

        val details = extractAuthDataFromHeaders(headers)

        logger.info(
            "[generateToken] => authenticating for " +
                    "${details.getAppId()}:${details.getAppSecret()}"
        )

        val token = authService.getOrGenerateToken(details)
        val expiresIn = token.getExpiration().time - Timestamp(System.currentTimeMillis()).time

        return ResponseEntity.ok(
            AccessTokenResponse(token.getValue(), expiresIn, token.getExpiration())
        )
    }


    private fun extractTokenFromHeader(header: String): String? {
        val token = header.removePrefix("Basic ")
        return if (token != header) token else null
    }

    private fun extractAuthDataFromHeaders(headers: HttpHeaders): ServiceDetails {

        val authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION) ?: run {
            logger.info("[generateToken] => invalid headers")
            throw AuthException("Auth header hasn't been found")
        }

        val token = extractTokenFromHeader(authHeader)
        logger.debug("[generateToken] => basic auth token: $token")

        val authData: String
        try {
            authData = StringUtils.newStringUtf8(Base64.getDecoder().decode(token))
        } catch (exc: IllegalArgumentException) {
            throw AuthException("Auth data must be encoded in base64")
        }
        logger.error("[generateToken] => auth data: $authData")

        val authDataParts = authData.split(":")
        if (authDataParts.size != 2) {
            throw AuthException("Auth data must have following format: appId:appSecret")
        }

        return ServiceDetailsDto(authDataParts[0], authDataParts[1])
    }


}
