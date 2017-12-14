package com.github.titovart.graal.hashtag.validation

import com.github.titovart.graal.hashtag.model.HashTag
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator


@Component
class HashTagValidator : Validator {

    override fun validate(target: Any?, errors: Errors) {

        val tag = target as HashTag

        if (!tag.value.matches(HASHTAG_REGEX)) {
            errors.reject("Invalid hashtag value.")
        }

    }

    override fun supports(clazz: Class<*>): Boolean {
        return HashTag::class.java.isAssignableFrom(clazz)
    }

    companion object {
        val HASHTAG_REGEX = Regex("^#\\w+$")
    }
}
