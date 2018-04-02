package com.github.titovart.graal.user.web

import com.github.titovart.graal.user.model.User
import com.github.titovart.graal.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("")
    fun findAll(pageable: Pageable): Page<User> {
        return service.findAll(pageable).also { page ->
            logger.info("[findAll($pageable)] => $page")
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): User {
        return service.findById(id).also { user -> logger.info("[findById($id)] => $user") }
    }

    @GetMapping("/find")
    fun findByUserName(@RequestParam("username") username: String): User {
        return service.findByUserName(username).also { user ->
            logger.info("[findByUserName($username)] => $user")
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid userRequest: User, resp: HttpServletResponse): ResponseEntity<Unit> {

        try {
            findByUserName(userRequest.nickName).let {
                throw EntityExistsException("User with this nickname already exists.")
            }
        } catch (exc: EntityNotFoundException) {
            // just check that creating entity is unique so do nothing here
        }

        service.save(userRequest).also { user ->
            resp.addHeader(HttpHeaders.LOCATION, "/users/${user.id}")
            logger.info("[create($userRequest)] => created")
        }

        return ResponseEntity(HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody userRequest: User): ResponseEntity<User> {
        val updatedUser = service.update(id, userRequest).also { user ->
            logger.info("[update($userRequest) => updated $user")
        }

        return ResponseEntity.ok(updatedUser)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        service.delete(id).also { logger.info("[delete($id)] => deleted") }
        return ResponseEntity.noContent().build()
    }
}