package com.github.titovart.spring.service.secure.server.details


class ServiceDetailsDto(
    private val appId: String,
    private val appSecret: String
) : ServiceDetails {

    override fun getAppSecret(): String = appSecret

    override fun getAppId(): String = appId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceDetailsDto

        if (appId != other.appId) return false
        if (appSecret != other.appSecret) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appId.hashCode()
        result = 31 * result + appSecret.hashCode()
        return result
    }

    override fun toString(): String {
        return "ServiceDetailsDto(appId='$appId', appSecret='$appSecret')"
    }

}