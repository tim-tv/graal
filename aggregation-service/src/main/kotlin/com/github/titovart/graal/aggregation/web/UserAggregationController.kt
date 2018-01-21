package com.github.titovart.graal.aggregation.web

import com.github.titovart.graal.aggregation.client.AuthClient
import com.github.titovart.graal.aggregation.client.PostClient
import com.github.titovart.graal.aggregation.client.TagClient
import com.github.titovart.graal.aggregation.client.UserClient
import com.github.titovart.graal.aggregation.entity.AuthResponse
import com.github.titovart.graal.aggregation.entity.PartialResponse
import com.github.titovart.graal.aggregation.entity.post.PostFeignRequest
import com.github.titovart.graal.aggregation.entity.post.PostPartialResponse
import com.github.titovart.graal.aggregation.entity.post.PostRequest
import com.github.titovart.graal.aggregation.entity.post.PostResponse
import com.github.titovart.graal.aggregation.entity.tag.Tag
import com.github.titovart.graal.aggregation.web.ExceptionController.Companion.AuthException
import com.netflix.client.ClientException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ServerErrorException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import javax.naming.ServiceUnavailableException
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/aggr")
class UserAggregationController(
        private val userClient: UserClient,
        private val postClient: PostClient,
        private val tagClient: TagClient,
        private val authClient: AuthClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private final lateinit var taskQueue: BlockingQueue<RecoveryTask>

    private final lateinit var taskExecutorService: ExecutorService

    @PostConstruct
    fun initExecutors() {
        taskQueue = ArrayBlockingQueue(MAX_TASK_QUEUE_SIZE)
        taskExecutorService = Executors.newSingleThreadExecutor()

        // detached thread for processing tasks from queue (see #updatePost)
        taskExecutorService.submit {
            while (true) {
                val task = taskQueue.take()
                logger.info("[executor]:queue[${taskQueue.size}] => " +
                        "Url: ${task.url}, Description: ${task.description}")

                try {
                    task.body()
                } catch (exc: ServiceUnavailableException) {
                    logger.info("[executor] => service is still unavailable: $exc")
                    taskQueue.put(task)
                    Thread.sleep(POOLING_UNAVAILABLE_TIMEOUT) // set timeout if the service is unavailable
                } catch (exc: Exception) {
                    logger.info("[executor] => errors $exc")
                }
            }
        }
    }

    @PostMapping("/users/{userId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(@PathVariable userId: Long,
                   @RequestBody postRequest: PostRequest,
                   @RequestHeader headers: HttpHeaders,
                   resp: HttpServletResponse): ResponseEntity<Any> {

        logger.info("[createPost($userId)] => checking scope permissions")
        val authResp = checkPermissionsAndGetAuthResponse(headers, listOf("ui"))

        val user = clientSafeGetExec { userClient.getById(userId) }
        logger.info("[createPost($userId)] => received User(id=$userId)")

        logger.info("[createPost($userId)] => checking user permissions")
        if (authResp.principal.username != user.nickName) {
            throw AuthException("This user hasn't permissions to call this method.")
        }

        logger.info("[createPost($userId)] => getting tags")
        val rollbackResp = createOrGetTags(postRequest.tags)
        rollbackResp.exc?.let {
            logger.info("[createPost($userId)] => rollback tags after ${rollbackResp.exc}")
            rollbackTags(rollbackResp.newValues.toSet())
            throw rollbackResp.exc
        }
        val tags = rollbackResp.values.map { it.id }.toSet()

        val postFeignRequest =
                PostFeignRequest(postRequest.content, postRequest.caption, user.id, tags)

        logger.info("[createPost($userId)] => creating a new post")
        val postResp = try {
            safeExec({ postClient.create(postFeignRequest) }, HttpStatus.CREATED)
        } catch (exc: Exception) {
            logger.info("[createPost($userId)] => rollback tags after $exc")
            rollbackTags(rollbackResp.newValues.toSet())
            throw exc
        }

        val locationHeader = postResp.headers.getFirst(HttpHeaders.LOCATION)
        resp.addHeader(HttpHeaders.LOCATION, locationHeader)

        logger.info("[createPost($userId)] => a new post has been created: $locationHeader")

        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping("/users/{userId}/posts")
    @ResponseStatus(HttpStatus.OK)
    fun getAllPosts(@PathVariable userId: Long,
                    pageable: Pageable,
                    @RequestHeader headers: HttpHeaders): Page<PostResponse> {

//        logger.info("[getAllPosts($userId)] => checking scope permissions")
//        checkPermissionsAndGetAuthResponse(headers, listOf("ui"))

        logger.info("[getAllPosts($userId)] => getting user")
        val user = clientSafeGetExec { userClient.getById(userId) }

        logger.info("[getAllPosts($userId)] => getting posts page")
        val postResp = exec { postClient.getByUserId(userId, pageable.pageNumber, pageable.pageSize) }

        val content = postResp.content.map {
            logger.info("[getAllPosts($userId)] => getting tags for Post(id=${it.id})")
            val tags = it.tags.map { tagId ->
                safeGetExec { tagClient.getById(tagId) }.value
            }.toMutableSet()

            PostResponse(it.id, it.content, it.caption, user, tags, it.createdDateTime, it.lastModifiedDateTime)
        }.toMutableList()

        logger.info("[getAllPosts($userId)] => all posts have been aggregated")
        val page = PageRequest.of(pageable.pageNumber, pageable.pageSize)
        return PageImpl(content, page, postResp.metadata?.totalElements ?: content.size.toLong())
    }

    @PutMapping("/users/{userId}/posts/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePost(@PathVariable userId: Long,
                   @PathVariable postId: Long,
                   @RequestBody postRequest: PostRequest,
                   @RequestHeader headers: HttpHeaders): ResponseEntity<Any> {

        logger.info("[updatePost($userId)] => checking scope permissions")
        val authResp = checkPermissionsAndGetAuthResponse(headers, listOf("ui"))

        val user = clientSafeGetExec { userClient.getById(userId) }
        logger.info("[updatePost($postId)] => received User(id=$userId)")


        logger.info("[updatePost($userId)] => checking user permissions")
        if (authResp.principal.username != user.nickName) {
            throw AuthException("This user hasn't permissions to call this method.")
        }

        val getTagsFunc = {
            val tags = postRequest.tags.map { getOrCreateTag(it).body.id }.toSet()
            logger.info("[updatePost($postId)] => all tags have received")
            tags
        }

        val updateTagsFunc = { tags: Set<Long> ->

            val postFeignRequest =
                    PostFeignRequest(postRequest.content, postRequest.caption, user.id, tags)

            clientSafeExec({ postClient.update(postId, postFeignRequest) })
            logger.info("[updatePost($postId)] => the post has been updated")
        }

        val fullUpdateFunc = {
            val tags = getTagsFunc()
            updateTagsFunc(tags)
        }

        val url = "/users/$userId/posts/$postId"
        val tags = try {
            getTagsFunc()
        } catch (exc: ServiceUnavailableException) {
            logger.info("[updatePost($postId)] => scheduling task because of: $exc")
            val state = taskQueue.offer(RecoveryTask(url, "[updatePost] => can't get tags", { fullUpdateFunc() }))
            if (!state) {
                logger.info("[updatePost($postId)] => couldn't schedule task because queue is full")
                throw exc
            }
            return ResponseEntity(HttpStatus.OK)
        }

        try {
            updateTagsFunc(tags)
        } catch (exc: ServiceUnavailableException) {
            logger.info("[updatePost($postId)] => schedule task because of: $exc")
            val state = taskQueue.offer(RecoveryTask(url, "[updatePost] => can't get tags", { fullUpdateFunc() }))
            if (!state) {
                logger.info("[updatePost($postId)] => couldn't schedule task because queue is full")
                throw exc
            }
            return ResponseEntity(HttpStatus.OK)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPostById(@PathVariable postId: Long, @RequestHeader headers: HttpHeaders): PartialResponse {

        logger.info("[getPostById($postId)] => checking permissions")
        checkPermissionsAndGetAuthResponse(headers, listOf("ui"))

        logger.info("[getPostById($postId)] => getting post by id = $postId")
        val post = clientSafeGetExec { postClient.getById(postId) }

        logger.info("[getPostById($postId)] => getting user by id = ${post.userId}")
        val user = safeGetExec { userClient.getById(post.userId) }

        val tags = post.tags.map { tagId ->
            logger.info("[getPostById($postId)] => getting tag by id = $tagId")
            try {
                safeGetExec { tagClient.getById(tagId) }.value
            } catch (exc: ServiceUnavailableException) {
                logger.error("[getPostById($postId)] => hashtag service is unavailable")
                val resp = PostPartialResponse.create(post, user, "/aggr/posts/$postId/tags")
                return PartialResponse(isPartial = true, body = resp)
            }
        }.toMutableSet()

        logger.info("[getPostById($postId)] => aggregated post has received")
        return PartialResponse(body = PostResponse.fromFeign(post, tags, user))
    }

    @GetMapping("/posts/{postId}/tags")
    @ResponseStatus(HttpStatus.OK)
    fun getTagsByPost(@PathVariable postId: Long,
                      @RequestHeader headers: HttpHeaders,
                      pageable: Pageable): Page<Tag> {

        logger.info("[getTagsByPost($postId)] => checking permissions")
        checkPermissionsAndGetAuthResponse(headers, listOf("ui"))

        logger.info("[getTagsByPost($postId)] => getting post by id = $postId")
        val post = clientSafeGetExec { postClient.getById(postId) }

        val tags = post.tags.map { tagId ->
            logger.info("[getTagsByPost($postId)] => getting tage by id = $tagId")
            safeGetExec { tagClient.getById(tagId) }
        }.toList()

        val start = maxOf(pageable.offset.toInt(), 0)
        val finish = minOf(start + pageable.pageSize - 1, tags.size - 1)
        val data = tags.slice(start..finish)

        logger.info("[getTagsByPost($postId)] => aggregated tags has received and wrapped to page")
        return PageImpl<Tag>(data, pageable, tags.size.toLong())
    }

    private fun rollbackTags(tags: Set<Tag>) {
        tags.map { tag ->
            logger.info("[rollbackTags] => deleting $tag")
            safeExec({ tagClient.delete(tag.id) }, HttpStatus.NO_CONTENT)
        }
    }

    private fun createOrGetTags(tagValues: Set<String>): RollbackResponse<Tag> {
        val tags = mutableListOf<Tag>()
        val newTags = mutableListOf<Tag>()

        tagValues.map { tagValue ->
            logger.info("[createOrGetTags] => getting tag by value = $tagValue")

            val tagProxy = try {
                getOrCreateTag(tagValue)
            } catch (exc: ServiceUnavailableException) {
                // rethrow exception because if service is unavailable it can't do rollback
                throw exc
            } catch (exc: HttpClientErrorException) {
                return RollbackResponse(tags, newTags, exc)
            }

            tags += tagProxy.body
            if (tagProxy.isNew) {
                newTags += tagProxy.body
            }
        }

        return RollbackResponse(tags, newTags)
    }

    private fun getOrCreateTag(tagValue: String): TagProxy {
        val tagResp = exec({ tagClient.getByValue(tagValue) }, HttpStatus.NOT_FOUND)
        if (tagResp.statusCode == HttpStatus.OK) {
            val tag = tagResp.body ?: throw ServerErrorException("Response body is null")
            return TagProxy(tag, false)
        }

        val newTagResp = clientSafeExec({ tagClient.create(Tag(value = tagValue)) }, HttpStatus.CREATED)
        val locationHeader = newTagResp.headers.getFirst(HttpHeaders.LOCATION)
                ?: throw ServerErrorException("Invalid location header")
        val id = locationHeader.split('/').last().toLong()
        return TagProxy(Tag(id, tagValue), true)
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

    private fun <T> exec(
            body: () -> ResponseEntity<T>,
            exceptStatus: HttpStatus = HttpStatus.OK): ResponseEntity<T> {

        return try {
            exec { body() }
        } catch (exc: HttpClientErrorException) {
            if (exc.statusCode == exceptStatus) {
                ResponseEntity(exceptStatus)
            } else {
                throw exc
            }
        }
    }

    private fun <T> clientSafeExec(
            body: () -> ResponseEntity<T>,
            validStatus: HttpStatus = HttpStatus.OK): ResponseEntity<T> {

        val resp = exec { body() }
        return resp.takeIf { it.statusCode == validStatus } ?:
                throw ServerErrorException("Expected status $validStatus, but got ${resp.statusCode}.")
    }

    private fun <T> clientSafeGetExec(body: () -> ResponseEntity<T>): T {
        return clientSafeExec(body).body ?: throw ServerErrorException("Response body is null.")
    }

    private fun <T> safeExec(
            body: () -> ResponseEntity<T>,
            validStatus: HttpStatus = HttpStatus.OK): ResponseEntity<T> {

        try {
            return clientSafeExec(body, validStatus)
        } catch (exc: HttpClientErrorException) {
            throw ServerErrorException("Status: ${exc.statusCode}, Message: ${exc.responseBodyAsString}")
        }
    }

    private fun <T> safeGetExec(body: () -> ResponseEntity<T>): T {
        try {
            return clientSafeGetExec(body)
        } catch (exc: HttpClientErrorException) {
            throw ServerErrorException("Status: ${exc.statusCode}, Message: ${exc.responseBodyAsString}")
        }
    }

    private fun checkPermissionsAndGetAuthResponse(headers: HttpHeaders, scopes: List<String>): AuthResponse {
        val token = headers.getFirst(HttpHeaders.AUTHORIZATION) ?:
                throw AuthException("Access token is not found.")

        val resp = exec { authClient.me(token) }.body
                ?: throw ServerErrorException("Response body is null.")

        val isValidScope = scopes.any { resp.oauth2Request.scope.contains(it) }

        if (!isValidScope) {
            logger.error("OAUTH SCOPE: ${resp.oauth2Request.scope}, METHOD SCOPE: $scopes")
            throw AuthException("Invalid permissions for this scope.")
        }

        return resp
    }

    companion object {
        data class RollbackResponse<T>(
                val values: List<T>,
                val newValues: List<T>,
                val exc: Throwable? = null
        )

        data class TagProxy(val body: Tag, val isNew: Boolean)

        data class RecoveryTask(val url: String, val description: String, val body: () -> Unit)

        const val MAX_TASK_QUEUE_SIZE = 1000

        const val POOLING_UNAVAILABLE_TIMEOUT = 10000L
    }
}
