package com.github.titovart.spring.service.secure.server.interceptor

import com.github.titovart.spring.service.secure.server.auth.AuthService
import com.github.titovart.spring.service.secure.server.controller.ExceptionController
import com.github.titovart.spring.service.secure.server.exception.AuthException
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class RequestInterceptor : HandlerInterceptorAdapter() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authService: AuthService

    override fun preHandle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        handler: Any?
    ): Boolean {

        val requestUri = request?.requestURI ?: throw AuthException()

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = authHeader.removePrefix("Bearer ")
        logger.debug("[preHandle] => handle uri $requestUri with auth header: $authHeader")

        if (!authService.validateToken(token)) {
            logger.debug("[preHandle] => handle uri $requestUri with auth header: $authHeader")
            response!!.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.status = 401
            val jsonObj = Gson().toJson(ExceptionController.ErrorResponse("Invalid access token"))
            response.writer.write(jsonObj)
            return false
        }

        return true

    }

}