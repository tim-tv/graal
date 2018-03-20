package com.github.titovart.spring.service.secure.server.exception

class AuthException(message: String? = "", cause: Throwable? = null) :
    RuntimeException(message, cause)
