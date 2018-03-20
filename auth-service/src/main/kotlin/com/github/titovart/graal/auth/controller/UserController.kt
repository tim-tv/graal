package com.github.titovart.graal.auth.controller

import com.github.titovart.graal.auth.domain.User
import com.github.titovart.graal.auth.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.validation.Valid


@RestController
class UserController(private val service: UserService) {

    @GetMapping("/me")
    fun getUser(principal: Principal): Principal = principal

    @PostMapping("/new")
    fun createUser(@Valid @RequestBody user: User) {
        service.create(user)
    }
}
