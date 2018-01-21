package com.github.titovart.spring.service.secure.server.details

interface ServiceDetailsStore {

    fun findDetailsByAppId(appId: String): ServiceDetails?

    fun save(details: ServiceDetails)

    fun remove(appId: String)
}