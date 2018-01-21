package com.github.titovart.spring.service.secure.server.controller

import com.github.titovart.spring.service.secure.server.exception.AuthException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(annotations = arrayOf(RestController::class))
class ExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    data class ErrorResponse(val message: String)

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthException::class)
    fun authExceptionHandler(exc: AuthException): ErrorResponse {
        logger.info("[401] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Authorization error")
    }

}