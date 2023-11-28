package life.offonoff.ab.web.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import life.offonoff.ab.util.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorizationTokenResolver {

    private final JwtProvider jwtProvider;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public Long resolveToken(HttpServletRequest request) {
        String authorizationToken = (String) request.getAttribute(AUTHORIZATION_HEADER);
        return getMemberId(authorizationToken);
    }

    private Long getMemberId(String authorizationToken) {
        String[] typeAndValue = authorizationToken.split(" ");
        String type = typeAndValue[0];
        String value = typeAndValue[1];

        if (type.equals(TokenType.BEARER)) {
            jwtProvider.parseMemberId(value);
        }

        throw new RuntimeException();
    }

    static class TokenType {
        final static String BEARER = "Bearer";
    }
}
