package com.github.titovart.graal.aggregation.web

import com.github.titovart.graal.aggregation.entity.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ServerErrorException
import javax.naming.ServiceUnavailableException
import javax.servlet.http.HttpServletRequest


@RestControllerAdvice(annotations = arrayOf(RestController::class))
class ExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(HttpServerErrorException::class)
    private fun handleServerErrorException(
            req: HttpServletRequest,
            exc: HttpServerErrorException): ResponseEntity<ErrorResponse> {

        logger.info("[${exc.statusCode}] => ", exc)
        return ResponseEntity(ErrorResponse(exc.responseBodyAsString), exc.statusCode)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    private fun handleClientErrorException(
            req: HttpServletRequest,
            exc: HttpClientErrorException): ResponseEntity<ErrorResponse> {

        logger.info("[${exc.statusCode}] => Status: ${exc.statusCode}, Message: ${exc.message}")
        return ResponseEntity(ErrorResponse(exc.responseBodyAsString), exc.statusCode)
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthException::class)
    fun handleAuthenticationException(exc: AuthException): ErrorResponse {
        logger.info("[401] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Unauthorized.")
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class)
    fun handleAuthenticationException(exc: ForbiddenException): ErrorResponse {
        logger.info("[403] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Forbidden.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleAuthenticationException(exc: HttpMediaTypeNotSupportedException): ErrorResponse {
        logger.info("[400] => ${exc.message}")
        return ErrorResponse(exc.message ?: "Invalid content type.")
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
        return ErrorResponse(exc.bindingResult.fieldError?.defaultMessage
                ?: "Invalid method arguments.")
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException::class)
    fun handleServiceUnavailableException(exc: ServiceUnavailableException): ErrorResponse {
        logger.error("[503] => ${exc.message}")
        return ErrorResponse("This request is unreachable because one " +
                "or more services are unavailable now. Please try later.")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerErrorException::class)
    fun handleServerErrorException(exc: ServerErrorException): ErrorResponse {
        logger.error("[500] => ", exc)
        return ErrorResponse("Internal service error. Please try later.")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(exc: Exception): ErrorResponse {
        logger.error("[500] => ", exc)
        return ErrorResponse("Internal service error. Please try later.")
    }

    companion object {
        class AuthException(msg: String) : RuntimeException(msg)

        class ForbiddenException(msg: String) : RuntimeException(msg)
    }

}
