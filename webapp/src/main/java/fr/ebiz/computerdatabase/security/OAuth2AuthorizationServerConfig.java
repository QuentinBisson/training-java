package fr.ebiz.computerdatabase.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Configuration for the OAuth2 authorization server.
 */
@Configuration
@EnableAuthorizationServer
@Import({SecurityConfiguration.class, SharedOAuth2Config.class})
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter accessTokenConverter;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor.
     *
     * @param tokenStore            inject the token store
     * @param accessTokenConverter  inject the access token converter
     * @param authenticationManager inject the authentication manager
     */
    @Autowired
    public OAuth2AuthorizationServerConfig(TokenStore tokenStore, JwtAccessTokenConverter accessTokenConverter, AuthenticationManager authenticationManager) {
        this.tokenStore = tokenStore;
        this.accessTokenConverter = accessTokenConverter;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Configure the authorization server.
     *
     * @param endpoints The authorization server endpoint configurer
     * @throws Exception If an exception occurs
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                .authenticationManager(authenticationManager);
    }

    /**
     * Create the token services.
     *
     * @return The token services
     */
    @Bean
    @Primary
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setRefreshTokenValiditySeconds(5);
        return defaultTokenServices;
    }
}