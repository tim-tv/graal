package com.github.titovart.graal.user.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.persistence.EntityNotFoundException


@RestControllerAdvice(annotations = arrayOf(RestController::class))
class ExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    data class ErrorResponse(val message: String)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(exc: EntityNotFoundException): ErrorResponse {
        logger.info("[404] => ${exc.message}")
        return ErrorResponse(exc.message ?: "entity not found")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(exc: Exception): ErrorResponse {
        logger.error("[500] => ${exc.message}")
        return ErrorResponse(exc.message ?: "unknown error")
    }
}
