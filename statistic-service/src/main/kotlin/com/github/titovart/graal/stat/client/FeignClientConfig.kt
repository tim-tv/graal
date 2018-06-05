package com.github.titovart.graal.stat.client

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignClientConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder = FeignClientErrorDecoder()

}
