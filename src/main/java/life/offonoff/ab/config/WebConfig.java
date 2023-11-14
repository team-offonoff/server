package life.offonoff.ab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //== OAUTH CORS ==//
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/oauth/**")
                .allowedOrigins("http://localhost:5173")
                .allowCredentials(true)
                .allowedMethods("GET", "POST");
    }
}
