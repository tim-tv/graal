package com.github.titovart.graal.auth

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.UserRepository
import com.github.titovart.graal.auth.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean


@SpringBootApplication
@EnableDiscoveryClient
class AuthServiceApplication {

    @Bean
    fun init(service: UserService, repository: UserRepository) = CommandLineRunner {
        repository.deleteAll()

        val user = User()
        user.setUsername("titart")
        user.setPassword("12345")
        service.create(user)
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AuthServiceApplication::class.java, *args)
        }
    }

}
