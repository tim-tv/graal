package com.github.titovart.graal.auth

import com.github.titovart.graal.auth.domain.Role
import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.repository.RoleRepository
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
    fun init(
        service: UserService,
        userRepository: UserRepository,
        roleRepository: RoleRepository
    ) =
        CommandLineRunner {
            userRepository.deleteAll()
            roleRepository.deleteAll()

            val userRole = roleRepository.save(Role("ROLE_USER"))
            val adminRole = roleRepository.save(Role("ROLE_ADMIN"))

            val user = User()
            user.setUsername("titart")
            user.setPassword("12345")
            user.setRoles(arrayListOf(userRole, adminRole))
            service.create(user)

            val user2 = User()
            user2.setUsername("murmur")
            user2.setPassword("qwerty")
            user2.setRoles(arrayListOf(userRole))
            service.create(user2)

            val user3 = User()
            user3.setUsername("mr_robot")
            user3.setPassword("topsecret")
            user3.setRoles(arrayListOf(userRole))
            service.create(user3)
        }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AuthServiceApplication::class.java, *args)
        }
    }

}
