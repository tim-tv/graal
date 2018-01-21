package com.github.titovart.graal.post

import com.github.titovart.spring.service.secure.server.EnableServiceSecureServer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableServiceSecureServer
class PostServiceApplication {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(PostServiceApplication::class.java, *args)
        }
    }

}
