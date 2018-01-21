package com.github.titovart.spring.service.secure.server.token

import java.sql.Timestamp


interface AccessToken {

    fun getAppId(): String

    fun getValue(): String

    fun getExpiration(): Timestamp


}