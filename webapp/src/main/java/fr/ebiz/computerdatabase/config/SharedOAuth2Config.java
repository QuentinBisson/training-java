package fr.ebiz.computerdatabase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class SharedOAuth2Config {

    /**
     * Create the JWT token store to configure OAuth2 to use JWT Tokens for the Resource server.
     *
     * @return The token store
     */
    @Bean(name = "resourceTokenStore")
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * Create tha access token converter.
     *
     * @return The access token converter
     */
    @Bean(name = "resourceTokenConverter")
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }


}
