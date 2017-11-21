package com.github.titovart.graal.gateway.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.naming.ServiceUnavailableException
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

@RestControllerAdvice(annotations = arrayOf(RestController::class))
class ExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    data class ErrorResponse(val message: String)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFoundException(exc: EntityNotFoundException): ErrorResponse {
        logger.info("[404] => ${exc.message}")
        return ErrorResponse(exc.message ?: "entity not found")
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException::class)
    fun handleEntityExistsException(exc: EntityExistsException): ErrorResponse {
        logger.info("[409] => ${exc.message}")
        return ErrorResponse(exc.message ?: "unknown error")
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException::class)
    fun handleServiceUnvailableException(exc: ServiceUnavailableException): ErrorResponse {
        logger.error("[503] => ${exc.message}")
        return ErrorResponse("Server is unvailable right now. Please try again later.")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(exc: Exception): ErrorResponse {
        logger.error("[500] => ", exc)
        return ErrorResponse("Internal server error")
    }

}