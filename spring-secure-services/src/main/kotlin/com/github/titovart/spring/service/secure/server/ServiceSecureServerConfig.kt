package com.github.titovart.spring.service.secure.server

import com.github.titovart.spring.service.secure.server.auth.AuthService
import com.github.titovart.spring.service.secure.server.auth.BasicAuthService
import com.github.titovart.spring.service.secure.server.controller.ServiceSecureServerController
import com.github.titovart.spring.service.secure.server.details.ServiceDetailsStore
import com.github.titovart.spring.service.secure.server.details.jdbc.JdbcServiceDetailsStore
import com.github.titovart.spring.service.secure.server.interceptor.RequestInterceptor
import com.github.titovart.spring.service.secure.server.token.AccessTokenStore
import com.github.titovart.spring.service.secure.server.token.jdbc.JdbcAccessTokenStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class ServiceSecureServerConfig : WebMvcConfigurer {

    @Autowired
    lateinit var requestInterceptor: HandlerInterceptor

    @Bean
    fun accessTokenStore(): AccessTokenStore {
        return JdbcAccessTokenStore()
    }

    @Bean
    fun serviceDetailsStore(): ServiceDetailsStore {
        return JdbcServiceDetailsStore()
    }

    @Bean
    fun authService(): AuthService {
        return BasicAuthService()
    }

    @Bean
    fun serverServiceController(): ServiceSecureServerController {
        return ServiceSecureServerController()
    }

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry!!.addInterceptor(requestInterceptor).excludePathPatterns("/token")
    }
}
