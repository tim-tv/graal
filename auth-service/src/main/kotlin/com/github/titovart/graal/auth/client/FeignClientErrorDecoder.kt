package com.github.titovart.graal.auth.client

import feign.FeignException.errorStatus
import feign.Response
import feign.codec.ErrorDecoder
import org.apache.commons.io.IOUtils
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.io.IOException


class FeignClientErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String, response: Response): Exception {

        val responseHeaders = HttpHeaders()
        response.headers().entries.stream().map { responseHeaders.put(it.key, ArrayList(it.value)) }

        val statusCode = HttpStatus.valueOf(response.status())
        val statusText = response.reason()

        val obj: JSONObject
        try {
            obj = JSONObject(IOUtils.toString(response.body().asReader()))
        } catch (exc: IOException) {
            throw RuntimeException("Failed to process response body.", exc)
        }

        val responseBody = try {
            // get error message from client services
            obj.getString("message").toByteArray()
        } catch (exc: JSONException) {
            // read error message from auth-service
            obj.getString("error_description").toByteArray()
        }

        return when (response.status()) {
            in 400..500 -> {
                HttpClientErrorException(
                    statusCode,
                    statusText,
                    responseHeaders,
                    responseBody,
                    null
                )
            }
            in 500..600 -> {
                HttpServerErrorException(
                    statusCode,
                    statusText,
                    responseHeaders,
                    responseBody,
                    null
                )
            }
            else -> errorStatus(methodKey, response)
        }
    }
}