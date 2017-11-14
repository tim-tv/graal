package com.github.titovart.graal.post.validation

import com.github.titovart.graal.post.model.Post
import com.github.titovart.graal.post.model.PostRequest
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class PostValidator : Validator {

    override fun validate(target: Any?, errors: Errors) {

        val post = if (target is Post) {
            PostRequest(target.content, target.caption, target.tags)
        } else {
            target as PostRequest
        }

        post.caption?.length?.let { len ->
            if (len > Post.CAPTION_LEN) {
                errors.rejectValue("caption",
                        "Length of post's caption must be less then ${Post.CAPTION_LEN}")
            }
        }

        post.content?.length?.let { len ->
            if (len > Post.CONTENT_LEN) {
                errors.rejectValue("content",
                        "Length of post's content must less then ${Post.CONTENT_LEN}")
            }
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return PostRequest::class.java.isAssignableFrom(clazz)
                || Post::class.java.isAssignableFrom(clazz)
    }
}
