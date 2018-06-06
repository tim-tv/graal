package com.github.titovart.graal.auth.controller

import com.github.titovart.graal.auth.client.UserClient
import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.entity.UserFeignRequest
import com.github.titovart.graal.auth.entity.UserRequest
import com.github.titovart.graal.auth.service.UserService
import com.netflix.client.ClientException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.naming.ServiceUnavailableException
import javax.validation.Valid


@RestController
class UserController(private val service: UserService, private val userFeignClient: UserClient) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/me")
    fun getUser(principal: Principal): Principal = principal

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody user: UserRequest) {
        logger.info("[registerUser] => $user")

        val registeredUser = service.create(User.create(user.username, user.email, user.password))
        logger.info("[registerUser($user) => a new user in auth-service has been created")

        val resp = exec { userFeignClient.create(UserFeignRequest(user.username, user.email)) }
        if (resp.statusCode != HttpStatus.CREATED) {
            logger.info(
                "Got invalid status while creating " +
                        "a new user in user-service: ${resp.statusCode}"
            )
            service.delete(registeredUser)
            logger.info("[registerUser($user) => an instance of registered user is rollbacked")
            throw ServiceUnavailableException()
        }
        logger.info("[registerUser($user) => a new user in user-service has been created")
    }

    private fun <T> exec(body: () -> T): T {
        try {
            return body()
        } catch (exc: RetryableException) {
            throw ServiceUnavailableException(exc.message)
        } catch (exc: RuntimeException) {
            // if ribbon can't connect to some service
            if (exc.cause is ClientException) {
                throw ServiceUnavailableException(exc.cause.toString())
            }
            throw exc
        }
    }

}
