package com.github.titovart.spring.service.secure.server.details

interface ServiceDetails {

    fun getAppSecret(): String

    fun getAppId(): String
}