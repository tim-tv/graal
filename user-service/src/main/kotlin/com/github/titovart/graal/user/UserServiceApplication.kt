package com.github.titovart.graal.user

import com.github.titovart.graal.user.model.User
import com.github.titovart.graal.user.repository.UserRepository
import com.github.titovart.graal.user.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean

@EnableEurekaClient
@SpringBootApplication
class UserServiceApplication {

    @Bean
    fun init(service: UserService, repository: UserRepository) = CommandLineRunner {
        repository.deleteAll()

        service.save(User("titart", "titart@gmail.com"))
        service.save(User("murmur", "murmur@gmail.com"))
        service.save(User("mr_robot", "mr.robot@gmail.com"))
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(UserServiceApplication::class.java, *args)
        }
    }

}


