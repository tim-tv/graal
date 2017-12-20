package com.github.titovart.graal.hashtag.web

import com.github.titovart.graal.hashtag.model.HashTag
import com.github.titovart.graal.hashtag.service.HashTagService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/hashtags")
class HashTagController(private val service: HashTagService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("")
    fun findAll(pageable: Pageable): Page<HashTag> {
        return service.findAll(pageable).also { page ->
            logger.info("[findAll($pageable)] => $page")
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): HashTag {
        return service.findById(id).also { tag -> logger.info("[findById($id)] => $tag") }
    }

    @GetMapping("/find")
    fun findByValue(@RequestParam("value") value: String): HashTag {
        return service.findByValue(value).also { tag ->
            logger.info("[findByValue($value)] => $tag")
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid tagRequest: HashTag, resp: HttpServletResponse): ResponseEntity<Unit> {
        service.save(tagRequest).also { tag ->
            resp.addHeader(HttpHeaders.LOCATION, "/hashtags/${tag.id}")
            logger.info("[create($tagRequest)] => created $tag")
        }

        return ResponseEntity(HttpStatus.CREATED)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        val tag = service.findById(id)

        service.delete(id).also { logger.info("[delete($id)] => deleted $tag") }
        return ResponseEntity.noContent().build()
    }
}
