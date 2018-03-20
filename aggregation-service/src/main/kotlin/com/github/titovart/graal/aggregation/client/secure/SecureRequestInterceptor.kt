package com.github.titovart.graal.aggregation.client.secure

import com.github.titovart.graal.aggregation.client.PostClient
import com.github.titovart.graal.aggregation.client.TagClient
import com.github.titovart.graal.aggregation.client.UserClient
import com.github.titovart.graal.aggregation.entity.TokenResponse
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

    @Autowired
    private lateinit var tagClient: TagClient

    @Autowired
    private lateinit var postClient: PostClient

    override fun apply(template: RequestTemplate?) {

        val method = template!!.request().url()
        logger.info("intercepting method: $method")

        when {
            method.startsWith("/users") -> setAuthHeader(userClient, template, "/users")
            method.startsWith("/posts") -> setAuthHeader(postClient, template, "/posts")
            method.startsWith("/hashtags") -> setAuthHeader(tagClient, template, "/hashtags")
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
        const val APP_ID = "aggregation-service-id"
        const val APP_SECRET = "aggregation-service-secret"

        const val EXPIRATION_SHIFT = 20_000
    }

}
