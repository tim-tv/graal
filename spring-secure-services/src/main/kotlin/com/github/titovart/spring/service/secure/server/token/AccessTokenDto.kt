package com.github.titovart.spring.service.secure.server.token

import java.sql.Timestamp


class AccessTokenDto(
    private val appId: String,
    private val value: String,
    private val expiration: Timestamp = Timestamp(System.currentTimeMillis())
) : AccessToken {

    override fun getAppId(): String = appId

    override fun getValue(): String = value

    override fun getExpiration(): Timestamp = expiration

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessTokenDto

        if (appId != other.appId) return false
        if (value != other.value) return false
        if (expiration != other.expiration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appId.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + expiration.hashCode()
        return result
    }

    override fun toString(): String {
        return "AccessTokenDto(appId='$appId', value='$value', expiration=$expiration)"
    }

}