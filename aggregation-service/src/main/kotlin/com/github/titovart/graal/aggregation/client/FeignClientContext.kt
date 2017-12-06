package com.github.titovart.graal.aggregation.client

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignClientContext {

    @Bean
    fun errorDecoder(): ErrorDecoder = FeignClientErrorDecoder()
}
