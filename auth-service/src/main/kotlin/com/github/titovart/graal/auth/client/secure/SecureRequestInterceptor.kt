package com.github.titovart.graal.auth.client.secure

import com.github.titovart.graal.auth.client.UserClient
import com.github.titovart.graal.auth.entity.TokenResponse
import feign.RequestInterceptor
import feign.RequestTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerErrorException
import java.sql.Timestamp
import java.util.*


class SecureRequestInterceptor : RequestInterceptor {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val tokenStore = mutableMapOf<String, TokenResponse>()

    @Autowired
    private lateinit var userClient: UserClient

    override fun apply(template: RequestTemplate?) {

        val method = template!!.request().url()
        logger.info("intercepting method: $method")

        when {
            method.startsWith("/users") -> setAuthHeader(userClient, template, "/users")
        }

    }

    private fun setAuthHeader(client: SecureClient, template: RequestTemplate, path: String) {
        var token = tokenStore[path]
        if (token == null || isTokenExpired(token)) {
            token = getToken(client, APP_ID, APP_SECRET)
        }
        template.header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}")
    }

    private fun isTokenExpired(token: TokenResponse): Boolean {
        return token.expiresAt.after(now(shift = EXPIRATION_SHIFT))
    }

    private fun now(shift: Int = 0) = Timestamp(System.currentTimeMillis() - shift)


    private fun getToken(client: SecureClient, appId: String, appSecret: String): TokenResponse {
        val auth = "$appId:$appSecret"
        val data = auth.toByteArray()
        val encodedData = Base64.getEncoder().encodeToString(data)
        val resp = client.getToken("Basic $encodedData")

        return resp.body ?: throw ServerErrorException("Auth token body from service is null")
    }

    companion object {
        const val APP_ID = "auth-service-id"
        const val APP_SECRET = "auth-service-secret"

        const val EXPIRATION_SHIFT = 20_000
    }

}
