package com.github.titovart.graal.aggregation.web

import com.github.titovart.graal.aggregation.client.PostClient
import com.github.titovart.graal.aggregation.client.TagClient
import com.github.titovart.graal.aggregation.client.UserClient
import com.github.titovart.graal.aggregation.entity.post.PostFeignRequest
import com.github.titovart.graal.aggregation.entity.post.PostFeignResponse
import com.github.titovart.graal.aggregation.entity.post.PostRequest
import com.github.titovart.graal.aggregation.entity.post.PostResponse
import com.github.titovart.graal.aggregation.entity.tag.Tag
import com.github.titovart.graal.aggregation.entity.user.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ServerErrorException
import javax.naming.ServiceUnavailableException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletResponse


class MessageResponseEntity<T>(
        status: HttpStatus,
        body: T? = null,
        headers: MultiValueMap<String, String>? = null,
        val message: String? = null) : ResponseEntity<T>(body, headers, status) {

    companion object {
        fun <T> fromResp(resp: ResponseEntity<T>): MessageResponseEntity<T> {
            return MessageResponseEntity(resp.statusCode, resp.body, resp.headers)
        }
    }

}


@RestController
@RequestMapping("/aggr")
class UserAggregationController(
        private val userClient: UserClient,
        private val postClient: PostClient,
        private val tagClient: TagClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/users/{userId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(@PathVariable userId: Long,
                   @RequestBody postRequest: PostRequest,
                   resp: HttpServletResponse): ResponseEntity<Any>
    {
        val user = getUser(userId)
        logger.info("[createPost($userId)] => received User(id=$userId)")

        val tags = postRequest.tags.map { getOrCreateTag(it).id }.toSet()
        logger.info("[createPost($userId)] => all tags received")

        val postFeignRequest =
                PostFeignRequest(postRequest.content, postRequest.caption, user.id, tags)

        val postResp = exec { postClient.create(postFeignRequest) }.let {
            if (it.statusCode != HttpStatus.CREATED) {
                throw ServerErrorException("Post has not been created. " +
                        "Http status: ${it.statusCode}, Message: ${it.message}.")
            }
            it
        }

        val locationHeader = postResp.headers.getFirst(HttpHeaders.LOCATION)
        resp.addHeader(HttpHeaders.LOCATION, locationHeader)

        logger.info("[createPost($userId)] => a new post has been created: $locationHeader")

        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping("/users/{userId}/posts")
    @ResponseStatus(HttpStatus.OK)
    fun getAllPosts(@PathVariable userId: Long, pageable: Pageable): Page<PostResponse> {

        val user = getUser(userId)

        val postResp = postClient.getByUserId(userId, pageable.pageNumber, pageable.pageSize)

        val content = postResp.content.map {
            val tags = it.tags.map {
                tagId -> getTagById(tagId)?.value ?:
                    throw ServerErrorException("Tag(id=$tagId) not found.")
            }.toMutableSet()

            PostResponse(it.id, it.content, it.caption, user, tags, it.createdDateTime, it.lastModifiedDateTime)
        }.toMutableList()

        val page = PageRequest.of(pageable.pageNumber, pageable.pageSize)
        return PageImpl(content, page, postResp.metadata?.totalElements ?: content.size.toLong())
    }


    @PutMapping("/users/{userId}/posts/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePost(@PathVariable userId: Long,
                   @PathVariable postId: Long,
                   @RequestBody postRequest: PostRequest,
                   resp: HttpServletResponse): ResponseEntity<Any>
    {
        val user = getUser(userId)
        logger.info("[updatePost($postId)] => received User(id=$userId)")

        val tags = postRequest.tags.map { getOrCreateTag(it).id }.toSet()
        logger.info("[updatePost($postId)] => all tags received")

        val postFeignRequest =
                PostFeignRequest(postRequest.content, postRequest.caption, user.id, tags)

        val postResp = exec { postClient.update(postId, postFeignRequest) }.let {
            when (it.statusCode) {
                HttpStatus.OK -> it
                HttpStatus.NOT_FOUND -> throw EntityNotFoundException("Post(id=$postId) not found.")
                else -> throw ServerErrorException("[updatePost($postId) => Post has not been " +
                        "updated. Http status: ${it.statusCode}, Message: ${it.message}.")
            }
        }

        logger.info("[updatePost($postId)] => the post has been updated")

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPostById(@PathVariable postId: Long): PostResponse {

        val post = getPost(postId)

        val user = try {
            getUser(post.userId)
        } catch (exc: EntityNotFoundException) {
            throw ServerErrorException(exc.message)
        }

        val tags = post.tags.map { tagId ->
            getTagById(tagId)?.value ?:
                    throw ServerErrorException("[getPostById($postId)] => Tag(id=$tagId) not found.")
        }.toMutableSet()

        return PostResponse.fromFeign(post, tags, user)
    }

    @GetMapping("/posts/{postId}/tags")
    @ResponseStatus(HttpStatus.OK)
    fun getTagsByPost(@PathVariable postId: Long, pageable: Pageable): Page<Tag> {

        val post = getPost(postId)

        val tags = post.tags.map { tagId ->
            getTagById(tagId) ?:
                    throw ServerErrorException("[getTagsByPost($postId)] => " +
                            "Tag(id=$tagId) not found.")
        }.toList()

        val start = maxOf(pageable.offset.toInt(), 0)
        val finish = minOf(start + pageable.pageSize - 1, tags.size - 1)
        val data = tags.slice(start..finish)

        return PageImpl<Tag>(data, pageable, tags.size.toLong())
    }

    private fun getPost(postId: Long): PostFeignResponse {
        val postResp = exec { postClient.getById(postId) }
        return when (postResp.statusCode) {
            HttpStatus.OK -> postResp.body ?:
                    throw ServerErrorException("Body of response for Post(id=$postId) is null.")
            HttpStatus.NOT_FOUND ->
                throw EntityNotFoundException(postResp.message)
            else -> throw ServerErrorException("Error while getting Post(id=$postId). " +
                    "Http status: ${postResp.statusCode}, Message: ${postResp.message}.")
        }
    }

    private fun getUser(userId: Long): UserResponse {
        val userResp = exec { userClient.getById(userId) }
        return when (userResp.statusCode) {
            HttpStatus.OK -> userResp.body ?:
                    throw ServerErrorException("Body of response for User(id=$userId) is null.")
            HttpStatus.NOT_FOUND ->
                throw EntityNotFoundException(userResp.message)
            else -> throw ServerErrorException("Error while getting User(id=$userId). " +
                    "Http status: ${userResp.statusCode}, Message: ${userResp.message}.")
        }
    }

    private fun getTagByValue(value: String): Tag? {
        val tagResp = exec { tagClient.getByValue(value) }
        return when(tagResp.statusCode) {
            HttpStatus.OK -> tagResp.body
            HttpStatus.NOT_FOUND -> null
            else -> throw ServerErrorException("Invalid status code - ${tagResp.statusCode} " +
                    "occurred while trying to retrieve tag #$value by value.")
        }
    }


    private fun getTagById(id: Long): Tag? {
        val tagResp = exec { tagClient.getById(id) }
        return when(tagResp.statusCode) {
            HttpStatus.OK -> tagResp.body
            HttpStatus.NOT_FOUND -> null
            else -> throw ServerErrorException("Invalid status code - ${tagResp.statusCode} " +
                    "occurred while trying to retrieve Tag(id=$id).")
        }
    }

    private fun getOrCreateTag(value: String): Tag {
        return getTagByValue(value) ?: let {
            val tagResp = exec { tagClient.create(Tag(value = value)) }
            if (tagResp.statusCode != HttpStatus.CREATED) {
                throw ServerErrorException("Couldn't create a new tag #$value. " +
                        "Http status: ${tagResp.statusCode}, Message: ${tagResp.message}.")
            }

            return getTagByValue(value) ?:
                    throw ServerErrorException("Couldn't get tag #$value after creating.")
        }
    }

    private fun <T> exec(body: () -> ResponseEntity<T>): MessageResponseEntity<T> {
        try {
            return MessageResponseEntity.fromResp(body())
        } catch (exc: HttpClientErrorException) {
            return MessageResponseEntity(status = exc.statusCode, message = exc.responseBodyAsString)
        } catch (exc: RuntimeException) {
            // FIXME: wtf can't catch ClientException here
            throw ServiceUnavailableException(exc.message)
        }
    }

}
