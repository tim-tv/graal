package com.github.titovart.graal.hashtag

import com.github.titovart.spring.service.secure.server.EnableServiceSecureServer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
@EnableServiceSecureServer
class HashtagServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(HashtagServiceApplication::class.java, *args)
        }
    }
}
