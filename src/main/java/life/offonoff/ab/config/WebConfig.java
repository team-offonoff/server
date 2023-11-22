package life.offonoff.ab.config;

import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthorizedArgumentResolver authorizedArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;

    //== OAUTH CORS ==//
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/oauth/**")
                .allowedOrigins("http://localhost:5173")
                .allowCredentials(true)
                // OPTION 요청은 preflight
                .allowedMethods("GET", "POST", "OPTION");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authorizedArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                // 적용 URI
                .addPathPatterns("/**")
                // 배제 URI
                .excludePathPatterns("/oauth/**", "/auth/**");
    }
}
