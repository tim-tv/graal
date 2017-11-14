package com.github.titovart.graal.post.web

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.model.PostRequest
import com.github.titovart.graal.post.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/posts")
class PostController(private val service: PostService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/")
    fun findAll(pageable: Pageable): Page<Post> {
        return service.findAll(pageable).also {
            page -> logger.info("[findAll($pageable)] => $page")
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): Post {
        return service.findById(id).also { post -> logger.info("[findById($id)] => $post") }
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody postRequest: Post, resp: HttpServletResponse): ResponseEntity<Unit> {
        service.save(postRequest).also {
            post ->
                resp.addHeader(HttpHeaders.LOCATION, "/posts/${post.id}")
                logger.info("[create($postRequest)] => created")
        }

        return ResponseEntity(HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody postRequest: PostRequest): ResponseEntity<Post> {
        val updatedUser = service.update(id, postRequest).also {
            post -> logger.info("[update($postRequest) => updated $post")
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