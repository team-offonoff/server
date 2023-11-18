package life.offonoff.ab.web.common.aspect.auth;

import life.offonoff.ab.web.common.auth.Authentication;
import life.offonoff.ab.web.common.auth.AuthenticationHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class AuthorizedArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationHolder authenticationHolder;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasAuthorizedAnnotation = parameter.hasParameterAnnotation(Authorized.class);
        boolean isMemberIdClass = parameter.getParameterType().equals(Long.class);

        return hasAuthorizedAnnotation && isMemberIdClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = authenticationHolder.getAuthentication();

        return authentication.getMemberId();
    }
}
