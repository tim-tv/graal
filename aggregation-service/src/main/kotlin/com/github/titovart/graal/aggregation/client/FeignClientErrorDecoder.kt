package com.github.titovart.graal.aggregation.client

import feign.FeignException.errorStatus
import feign.Response
import feign.codec.ErrorDecoder
import org.apache.commons.io.IOUtils
import org.codehaus.jettison.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.io.IOException
import java.lang.Exception


class FeignClientErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String, response: Response): Exception {

        val responseHeaders = HttpHeaders()
        response.headers().entries.stream().map { responseHeaders.put(it.key, ArrayList(it.value)) }

        val statusCode = HttpStatus.valueOf(response.status())
        val statusText = response.reason()

        val responseBody: ByteArray
        try {
            val obj = JSONObject(IOUtils.toString(response.body().asReader()))
            responseBody = obj.getString("message").toByteArray()
        } catch (e: IOException) {
            throw RuntimeException("Failed to process response body.", e)
        }

        return when (response.status()) {
            in 400..500 -> {
                HttpClientErrorException(statusCode, statusText, responseHeaders, responseBody, null)
            }
            in 500..600 -> {
                HttpServerErrorException(statusCode, statusText, responseHeaders, responseBody, null)
            }
            else -> errorStatus(methodKey, response)
        }
    }
}