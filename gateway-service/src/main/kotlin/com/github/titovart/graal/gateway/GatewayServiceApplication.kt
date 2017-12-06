package com.github.titovart.graal.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy


@SpringBootApplication
@EnableZuulProxy
class GatewayServiceApplication {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(GatewayServiceApplication::class.java, *args)
        }
    }

}
