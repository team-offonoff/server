package life.offonoff.ab.web.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import life.offonoff.ab.exception.auth.EmptyAuthorizationException;
import life.offonoff.ab.exception.auth.UnsupportedAuthFormatException;
import life.offonoff.ab.exception.auth.token.UnsupportedAuthTokenTypeException;
import life.offonoff.ab.util.token.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class AuthorizationTokenResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TOKEN_DELIMITER = " ";

    private final TokenProvider tokenProvider;

    //== resolve ==//
    public Long resolveToken(HttpServletRequest request) {
        String authToken = request.getHeader(AUTHORIZATION_HEADER);

        TokenTypeAndValue typeAndValue = getAuthToken(authToken);

        return getMemberId(typeAndValue);
    }

    //== Support methods & classes ==//
    private Long getMemberId(TokenTypeAndValue typeAndValue) {

        if (typeAndValue.isTypeOf(TokenType.BEARER)) {
            return tokenProvider.getMemberIdFromAccessToken(typeAndValue.getTokenValue());
        }

        throw new UnsupportedAuthTokenTypeException(typeAndValue.getTokenType());
    }

    private TokenTypeAndValue getAuthToken(String authToken) {

        validateAuthToken(authToken);

        return bindTokenTypeAndValue(authToken);
    }

    private void validateAuthToken(String authToken) {
        // not null
        if (!StringUtils.hasText(authToken)) {
            throw new EmptyAuthorizationException();
        }

        // tokenType + " " + tokenValue 형식 확인
        String[] tokenElements = authToken.split(AUTHORIZATION_TOKEN_DELIMITER);
        if (tokenElements.length != 2) {
            throw new UnsupportedAuthFormatException(authToken);
        }
    }

    private TokenTypeAndValue bindTokenTypeAndValue(String authToken) {
        String[] tokenElements = authToken.split(" ");

        String tokenType = tokenElements[0];
        String tokenValue = tokenElements[1];

        return new TokenTypeAndValue(tokenType, tokenValue);
    }

    @Getter
    static class TokenTypeAndValue {
        final String tokenType;
        final String tokenValue;

        TokenTypeAndValue(String type, String value) {
            this.tokenType = type;
            this.tokenValue = value;
        }

        boolean isTypeOf(String type) {
            return tokenType.equals(type);
        }
    }

    static class TokenType {
        final static String BEARER = "Bearer";
    }
}
