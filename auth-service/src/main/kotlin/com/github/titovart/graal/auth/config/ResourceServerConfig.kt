package com.github.titovart.graal.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter

@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http!!.cors().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .requestMatchers().antMatchers("/me", "/new")
            .and()
            .authorizeRequests()
            .antMatchers("/me").access("#oauth2.hasScope('ui') or (#oauth2.hasScope('api'))")
            .antMatchers("/new").access("#oauth2.hasScope('ui')")

    }
}
