package com.github.titovart.spring.service.secure.server.token

interface AccessTokenStore {

    fun findTokenByAppId(appId: String): AccessToken?

    fun findTokenByValue(tokenValue: String): AccessToken?

    fun saveToken(token: AccessToken)

    fun removeToken(appId: String)

}