package life.offonoff.ab.web.common.aspect.auth;

import jakarta.servlet.http.HttpServletRequest;
import life.offonoff.ab.exception.auth.EmptyAuthorizationException;
import life.offonoff.ab.web.common.auth.AuthorizationTokenResolver;
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

    private static final Class<Authorized> TARGET_ANNOTATION_CLASS = Authorized.class;

    private final AuthorizationTokenResolver tokenResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasAuthorizedAnnotation = parameter.hasParameterAnnotation(TARGET_ANNOTATION_CLASS);
        boolean isMemberIdClass = parameter.getParameterType()
                                           .equals(Long.class);

        return hasAuthorizedAnnotation && isMemberIdClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        try
        {
            return tokenResolver.resolveToken(request);
        }
        catch (EmptyAuthorizationException emptyEx)
        {
            if (isNullableParameter(parameter)) {
                return null;
            }
            throw emptyEx;
        }
    }

    private boolean isNullableParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(TARGET_ANNOTATION_CLASS)
                        .nullable();
    }
}
