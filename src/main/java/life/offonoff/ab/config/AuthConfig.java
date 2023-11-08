package life.offonoff.ab.config;

import life.offonoff.ab.util.jwt.token.JwtGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwtGenerator jwtGenerator() {
        return new JwtGenerator();
    }
}
