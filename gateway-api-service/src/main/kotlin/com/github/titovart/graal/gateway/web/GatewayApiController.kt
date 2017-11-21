package com.github.titovart.graal.gateway.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import javax.naming.ServiceUnavailableException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletResponse

data class User(val id: Long, val nickName: String, val email: String)

data class PostRequest(val caption: String, val content: String, val tags: Set<String>)

data class Post(val id: Long, val caption: String, val content: String, val tags: List<Long>)

data class Tag(val value: String, val id: Long = -1)


@RestController
@RequestMapping("/api")
class GatewayApiController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val restTemplate = RestTemplate()

    @PostMapping("/users/{id}/posts/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(@PathVariable id: Long,
                   @RequestBody postRequest: PostRequest,
                   resp: HttpServletResponse): ResponseEntity<Unit>
    {
        val userId = getUserById(id).id

        val tagIds: List<Long>
        try {
            tagIds = postRequest.tags.map { createOrGetTag(it).id }
        } catch (exc: HttpServerErrorException) {
            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw ServiceUnavailableException("HashTags service is unavailable")
            }
            throw exc
        }

        val post = Post(userId, postRequest.caption, postRequest.content, tagIds)

        val postResp: ResponseEntity<Any>
        try {
            postResp = restTemplate.postForEntity(buildPath("/api/internal/posts/"), post)
        } catch (exc: HttpServerErrorException) {
            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw ServiceUnavailableException("Posts service is unavailable")
            }
            throw exc
        }

        resp.addHeader(HttpHeaders.LOCATION, postResp.headers.location.toString())
        logger.info("[create post($postRequest)] => created ${postResp.headers.location}")
        return ResponseEntity(HttpStatus.CREATED)
    }

    private inline fun getBaseUri(): URI {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri()
    }

    private inline fun buildPath(relativePath: String): String {
        return "${getBaseUri()}$relativePath"
    }

    private fun getTagByValue(value: String): Tag =
        restTemplate.getForObject(buildPath("/api/tags/search?value=$value"), Tag::class.java) ?:
                throw RuntimeException("Null result for query: GET /api/tags/search?value=$value")

    private fun createOrGetTag(tagValue: String): Tag {
        return try {
            getTagByValue(tagValue)
        } catch (exc: HttpClientErrorException) {

            // try to create a new tag only if the tag with the given value not found
            if (exc.statusCode != HttpStatus.NOT_FOUND) {
                throw exc
            }

            val resp: ResponseEntity<Any> = restTemplate.postForEntity(buildPath("/api/tags/"), Tag(tagValue))

            if (resp.statusCode != HttpStatus.CREATED) {
                throw HttpServerErrorException(resp.statusCode, "Tag hasn't been created")
            } else {
                getTagByValue(tagValue)
            }
        }
    }

    private fun getUserById(id: Long): User {
        val user: User?
        try {
            user = restTemplate.getForObject(buildPath("/api/users/$id"), User::class)
        } catch (exc: HttpServerErrorException) {
            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw ServiceUnavailableException("Users service is unavailable")
            }
            throw exc
        } catch (exc: HttpClientErrorException) {
            if (exc.statusCode == HttpStatus.NOT_FOUND) {
                throw EntityNotFoundException("User(id=$id) not found")
            }
            throw exc
        }

        return user ?: throw EntityNotFoundException("User(id=$user.id) not found")
    }
}