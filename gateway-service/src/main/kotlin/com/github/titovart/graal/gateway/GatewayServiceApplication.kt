package com.github.titovart.graal.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy


@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
class GatewayServiceApplication {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(GatewayServiceApplication::class.java, *args)
        }
    }

}
