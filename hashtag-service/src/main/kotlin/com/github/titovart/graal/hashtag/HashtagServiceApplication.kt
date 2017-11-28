package com.github.titovart.graal.hashtag

import com.github.titovart.graal.hashtag.model.HashTag
import com.github.titovart.graal.hashtag.service.HashTagService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean

@EnableEurekaClient
@SpringBootApplication
class HashtagServiceApplication {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun init(service: HashTagService) = CommandLineRunner {
        service.save(HashTag("дочкисыночки"))
        service.save(HashTag("молодаямама"))
        service.save(HashTag("инстамама"))
        service.save(HashTag("маминарадость"))
        service.save(HashTag("сыночка"))
        service.save(HashTag("инстадочка"))

        logger.info("Hash tag micro-service has started")
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(HashtagServiceApplication::class.java, *args)
        }
    }
}
