package com.github.titovart.graal.gateway.web

import com.github.titovart.graal.gateway.client.PostClient
import com.github.titovart.graal.gateway.client.TagClient
import com.github.titovart.graal.gateway.client.UserClient
import com.github.titovart.graal.gateway.entity.post.PostRequest
import com.github.titovart.graal.gateway.entity.post.PostResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.PagedResources
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import javax.annotation.PostConstruct
import javax.naming.ServiceUnavailableException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.QueryParam

data class User(val id: Long, val nickName: String, val email: String)


data class Post(val id: Long, val caption: String, val content: String, val tags: List<Long>)

data class Tag(val value: String, val id: Long = -1)

class PagedPostRef : ParameterizedTypeReference<PagedResources<Post>>()

@RestController
@RequestMapping("/api")
class GatewayApiController(
        private val postClient: PostClient,
        private val userClient: UserClient,
        private val tagClient: TagClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val restTemplate = RestTemplate()


    @GetMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun findPostByUserId(@PathVariable id: Long): PostResponse {
        return postClient.getPostById(id)
    }
//
//    @GetMapping("/posts/search")
//    @ResponseStatus(HttpStatus.OK)
//    fun findPostsByUserId(@RequestParam("user_id") userId: Long): PagedResources<PostResponse> {
//        return postClient.getPostsByUserId(userId)
//    }
//
//    @PostMapping("/users-agg/{id}/posts")
//    @ResponseStatus(HttpStatus.CREATED)
//    fun createPostByUser(@PathVariable id: Long,
//                         @RequestBody postRequest: PostRequest,
//                         resp: HttpServletResponse): ResponseEntity<Unit>
//    {
//        val user = userClient.getUserById(id)
//
//        logger.info(user.toString())
//
//        val postDto = postClient.getPostsByUserId(user.id)
//
//        logger.info(postDto.toString())
//
//        throw EntityNotFoundException()
//    }
//
////    @PostMapping("/users/{id}/posts")
////    @ResponseStatus(HttpStatus.CREATED)
////    fun createPostByUser2(@PathVariable id: Long,
////                   @RequestBody postRequest: PostRequest,
////                   resp: HttpServletResponse): ResponseEntity<Unit>
////    {
////        val userId = getUserById(id).id
////
////        val tagIds: List<Long>
////        try {
////            tagIds = postRequest.tags.map { createOrGetTag(it).id }
////        } catch (exc: HttpServerErrorException) {
////            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
////                throw ServiceUnavailableException("HashTags service is unavailable")
////            }
////            throw exc
////        }
////
////        val post = Post(userId, postRequest.caption, postRequest.content, tagIds)
////
////        val postResp: ResponseEntity<Any>
////        try {
////            postResp = restTemplate.postForEntity(buildPath("/api/internal/posts/"), post)
////        } catch (exc: HttpServerErrorException) {
////            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
////                throw ServiceUnavailableException("Posts service is unavailable")
////            }
////            throw exc
////        }
////
////        resp.addHeader(HttpHeaders.LOCATION, postResp.headers.location.toString())
////        logger.info("[create post($postRequest)] => created ${postResp.headers.location}")
////        return ResponseEntity(HttpStatus.CREATED)
////    }
////
//
//    @GetMapping("/users23/{id}/posts")
//    @ResponseStatus(HttpStatus.OK)
//    fun getPostsByUser(@PathVariable id: Long, pageable: Pageable): Page<Post> {
//        val userId = getUserById(id).id
//
//        logger.info("Pageable: $pageable")
//
//        val url = UriComponentsBuilder.fromUriString(buildPath("/api/posts/search"))
//                .queryParam("user_id", id)
//                .queryParam("page", pageable.pageNumber)
//                .queryParam("offset", pageable.offset)
//                .queryParam("limit", pageable.pageSize)
//                .queryParam("sort", pageable.sort).build().toUri()
//
//        val postPageResp: ResponseEntity<PagedResources<Post>> =
//                execWithAvailableSafety({
//                    restTemplate.exchange(url, HttpMethod.GET, null, PagedPostRef())
//                }, "")
//
//        val page = Page.empty<Post>(pageable)
//
//        return PageImpl<Post>(postPageResp.body?.content?.toMutableList() ?: emptyList(), pageable, postPageResp.body?.metadata?.totalElements ?: 0)
//
//    }
//
//    private fun getBaseUri(): URI {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri()
//    }
//
//    private fun buildPath(relativePath: String): String {
//        return "${getBaseUri()}$relativePath"
//    }
//
//    private fun getTagByValue(value: String): Tag =
//        restTemplate.getForObject(buildPath("/api/tags/search?value=$value"), Tag::class.java) ?:
//                throw RuntimeException("Null result for query: GET /api/tags/search?value=$value")
//
//    private fun createOrGetTag(tagValue: String): Tag {
//        return try {
//            getTagByValue(tagValue)
//        } catch (exc: HttpClientErrorException) {
//
//            // try to create a new tag only if the tag with the given value not found
//            if (exc.statusCode != HttpStatus.NOT_FOUND) {
//                throw exc
//            }
//
//            val resp: ResponseEntity<Any> = restTemplate.postForEntity(buildPath("/api/tags/"), Tag(tagValue))
//
//            if (resp.statusCode != HttpStatus.CREATED) {
//                throw HttpServerErrorException(resp.statusCode, "Tag hasn't been created")
//            } else {
//                getTagByValue(tagValue)
//            }
//        }
//    }
//
//    private fun getUserById(id: Long): User {
//        val user: User?
//        try {
//            user = restTemplate.getForObject(buildPath("/api/users/$id"), User::class)
//        } catch (exc: HttpServerErrorException) {
//            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
//                throw ServiceUnavailableException("Users service is unavailable")
//            }
//            throw exc
//        } catch (exc: HttpClientErrorException) {
//            if (exc.statusCode == HttpStatus.NOT_FOUND) {
//                throw EntityNotFoundException("User(id=$id) not found")
//            }
//            throw exc
//        }
//
//        return user ?: throw EntityNotFoundException("User(id=$user.id) not found")
//    }
//
//
//    private fun <T> execWithAvailableSafety(query: () -> T, msg: String): T {
//        try {
//            return query()
//        } catch (exc: HttpServerErrorException) {
//            if (exc.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
//                throw ServiceUnavailableException(msg)
//            }
//            throw exc
//        }
//    }
}