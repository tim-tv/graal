package com.github.titovart.graal.aggregation

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients


@EnableRabbit
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
class AggregationServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AggregationServiceApplication::class.java, *args)
        }
    }
}
