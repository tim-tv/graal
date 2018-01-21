package com.github.titovart.graal.aggregation.client

import com.github.titovart.graal.aggregation.client.secure.SecureRequestInterceptor
import feign.RequestInterceptor
import feign.RequestTemplate
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignClientConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder = FeignClientErrorDecoder()

    @Bean
    fun secureRequestInterceptor(): RequestInterceptor = SecureRequestInterceptor()
}
