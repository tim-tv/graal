package com.github.titovart.graal.stat

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient


@EnableRabbit
@SpringBootApplication
@EnableEurekaClient
class StatisticServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(StatisticServiceApplication::class.java, *args)
        }
    }
}
