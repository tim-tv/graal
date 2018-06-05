package com.github.titovart.graal.stat.web

import com.github.titovart.graal.stat.client.AuthClient
import com.github.titovart.graal.stat.client.AuthResponse
import com.github.titovart.graal.stat.client.Authority
import com.github.titovart.graal.stat.domain.TagUsageStatistic
import com.github.titovart.graal.stat.service.TagUsageStatService
import com.netflix.client.ClientException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerErrorException
import javax.naming.ServiceUnavailableException


@RestController
@RequestMapping
class StatisticController(
    private val service: TagUsageStatService,
    private val authClient: AuthClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/tags/top")
    fun findTopUsedTags(
        pageable: Pageable,
        @RequestHeader headers: HttpHeaders
    ): Page<TagUsageStatistic> {

        logger.info("[findTopUsedTags($pageable)] => checking permissions")
        checkPermissionsAndGetAuthResponse(headers, ROLE_ADMIN, ALL_SCOPES)

        return service.findTopUsedTag(pageable).also { page ->
            logger.info("[findTopUsedTags($pageable)] => page has fetched ${page.pageable}")
        }
    }

    private fun <T> exec(body: () -> T): T {
        try {
            return body()
        } catch (exc: RetryableException) {
            throw ServiceUnavailableException(exc.message)
        } catch (exc: RuntimeException) {
            // if ribbon can't connect to some service
            if (exc.cause is ClientException) {
                throw ServiceUnavailableException(exc.cause.toString())
            }
            throw exc
        }
    }

    private fun checkPermissionsAndGetAuthResponse(
        headers: HttpHeaders,
        authority: Authority,
        scopes: List<String>
    ): AuthResponse {
        val token = headers.getFirst(HttpHeaders.AUTHORIZATION)
                ?: throw ExceptionController.Companion.AuthException("Access token is not found.")

        val resp = exec { authClient.me(token) }.body
                ?: throw ServerErrorException("Response body is null.")

        val isValidScope = scopes.any { resp.oauth2Request.scope.contains(it) }

        if (!isValidScope) {
            throw ExceptionController.Companion.ForbiddenException("Invalid permissions for this scope.")
        }

        val isValidAuthority = resp.principal.authorities.contains(authority)
        if (!isValidAuthority) {
            throw ExceptionController.Companion.ForbiddenException("Invalid permissions for this role.")
        }

        return resp
    }

    companion object {
        private val ROLE_ADMIN = Authority("ROLE_ADMIN")
        private val ALL_SCOPES = listOf("ui", "api")
    }

}
