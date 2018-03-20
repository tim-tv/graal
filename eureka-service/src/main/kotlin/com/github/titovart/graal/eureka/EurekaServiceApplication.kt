package com.github.titovart.graal.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer


@EnableEurekaServer
@SpringBootApplication
class EurekaServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(EurekaServiceApplication::class.java, *args)
        }
    }
}
