package com.github.titovart.graal.stat.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException


@RestControllerAdvice(annotations = [RestController::class])
class ExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    data class ErrorResponse(val message: String)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(exc: EntityNotFoundException): ErrorResponse {
        logger.info("[404] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Entity not found.")
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException::class)
    fun handleEntityExistsException(exc: EntityExistsException): ErrorResponse {
        logger.info("[409] => ${exc.message}")
        return ErrorResponse(exc.message ?: "This entity already exists.")
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleRequestMethodNotSupportedException(exc: HttpRequestMethodNotSupportedException): ErrorResponse {
        logger.info("[405] => ${exc.message}")
        return ErrorResponse(exc.message ?: "This method is not allowed.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleRequestParamException(exc: MissingServletRequestParameterException): ErrorResponse {
        logger.info("[400] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Missing request parameter.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMallformedJsonError(exc: HttpMessageNotReadableException): ErrorResponse {
        logger.info("[400] => ${exc.message}")
        return ErrorResponse("Malformed JSON. Please check request body and try again.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeException(exc: MethodArgumentTypeMismatchException): ErrorResponse {
        logger.info("[400] => ${exc.message}")
        return ErrorResponse("Invalid method argument type.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exc: MethodArgumentNotValidException): ErrorResponse {
        logger.info("[400] => ${exc.bindingResult}")
        return ErrorResponse(
            exc.bindingResult.fieldError?.defaultMessage ?: "Invalid method arguments"
        )
    }

}
