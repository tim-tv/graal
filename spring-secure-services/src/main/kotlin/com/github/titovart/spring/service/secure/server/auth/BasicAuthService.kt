package com.github.titovart.spring.service.secure.server.auth

import com.github.titovart.spring.service.secure.server.details.ServiceDetails
import com.github.titovart.spring.service.secure.server.details.ServiceDetailsStore
import com.github.titovart.spring.service.secure.server.exception.AuthException
import com.github.titovart.spring.service.secure.server.token.AccessToken
import com.github.titovart.spring.service.secure.server.token.AccessTokenDto
import com.github.titovart.spring.service.secure.server.token.AccessTokenStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.DigestUtils
import java.sql.Timestamp
import java.util.*


class BasicAuthService : AuthService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var tokenStore: AccessTokenStore

    @Autowired
    private lateinit var detailsStore: ServiceDetailsStore

    private val random = Random()

    override fun getOrGenerateToken(serviceDetails: ServiceDetails): AccessToken {
        val details = detailsStore.findDetailsByAppId(serviceDetails.getAppId()) ?: run {
            throw AuthException("Invalid app id")
        }

        if (details == serviceDetails) {
            logger.info("[generateToken($serviceDetails) => service details is valid")
        } else {
            throw AuthException("Invalid app secret")
        }

        var token = tokenStore.findTokenByAppId(details.getAppId())

        if (token == null || checkTokenExpiration(token)) {
            if (token != null) {
                tokenStore.removeToken(details.getAppId())
            }

            val newToken = generateToken(details)
            tokenStore.saveToken(newToken)
            logger.info("[generateToken($serviceDetails) => new token has been generated")
            token = newToken
        }

        return token
    }

    override fun validateToken(tokenValue: String): Boolean {
        val token = tokenStore.findTokenByValue(tokenValue) ?: run {
            logger.debug("[validateToken($tokenValue) => token hasn't been found")
            return false
        }

        return !checkTokenExpiration(token)
    }

    private fun checkTokenExpiration(token: AccessToken): Boolean {
        return token.getExpiration().before(Timestamp(System.currentTimeMillis()))
    }

    private fun generateToken(serviceDetails: ServiceDetails): AccessToken {
        val data =
            "${serviceDetails.getAppId()}:${serviceDetails.getAppSecret()}:${random.nextFloat()}"
        val token = DigestUtils.md5DigestAsHex(data.toByteArray())
        val expiresAt = Timestamp(System.currentTimeMillis() + EXPIRATION_TIME)
        return AccessTokenDto(serviceDetails.getAppId(), token, expiresAt)
    }

    companion object {
        val EXPIRATION_TIME = 1_800_000
    }
}
