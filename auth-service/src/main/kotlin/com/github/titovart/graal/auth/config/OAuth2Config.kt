package com.github.titovart.graal.auth.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import javax.sql.DataSource


@Configuration
@EnableAuthorizationServer
class OAuth2Config : AuthorizationServerConfigurerAdapter() {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var dataSource: DataSource


    override fun configure(clients: ClientDetailsServiceConfigurer?) {
        clients!!.jdbc(dataSource)
                .withClient("ui-client").secret("ui-client-secret")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(1_800) // 30 minutes
                .scopes("ui")
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer?) {
        endpoints!!.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer?) {
        security!!.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
    }
}
