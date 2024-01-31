package life.offonoff.ab.config;

import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static life.offonoff.ab.application.service.common.LengthInfo.PAGEABLE_SIZE;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthorizedArgumentResolver authorizedArgumentResolver;

    //== CORS ==//
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://offonoff.me")
                .allowCredentials(true)
                // OPTION 요청은 preflight
                .allowedMethods("GET", "POST", "OPTION");
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // processing @Authorized
        resolvers.add(authorizedArgumentResolver);

        // pageable
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setMaxPageSize(PAGEABLE_SIZE.getMaxLength());
        resolvers.add(pageableResolver);
    }
}
