package com.github.titovart.graal.auth

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.RoleRepository
import com.github.titovart.graal.auth.repository.UserRepository
import com.github.titovart.graal.auth.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean


@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
class AuthServiceApplication {

    @Bean
    fun init(
        service: UserService,
        userRepository: UserRepository,
        roleRepository: RoleRepository
    ) =
        CommandLineRunner {
            userRepository.deleteAll()
            roleRepository.deleteAll()

            service.create(User.createAdmin("titart", "avtitv@gmail.com", "12345"))
            service.create(User.create("mr_robot", "mr.robot@gmail.com", "evilcorp"))
            service.create(User.create("putin", "vv.putin@gov.ru", "topone"))
        }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AuthServiceApplication::class.java, *args)
        }
    }

}
