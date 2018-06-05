package com.github.titovart.graal.stat

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients


@EnableRabbit
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
class StatisticServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(StatisticServiceApplication::class.java, *args)
        }
    }
}
