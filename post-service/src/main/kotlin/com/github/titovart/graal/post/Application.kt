package com.github.titovart.graal.post

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.service.PostService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableDiscoveryClient
class Application {

    @Bean
    fun init(service: PostService) = CommandLineRunner {
        service.save(Post("Post content #1", "Caption #1", 1, mutableSetOf(1L, 2L)))
        service.save(Post("Post content #2", "Caption #2", 1))
        service.save(Post("Post content #3", "Caption #3", 2))
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

}
