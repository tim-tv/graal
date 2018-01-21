package com.github.titovart.spring.service.secure.server

import org.springframework.context.annotation.Import


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(ServiceSecureServerConfig::class)
annotation class EnableServiceSecureServer