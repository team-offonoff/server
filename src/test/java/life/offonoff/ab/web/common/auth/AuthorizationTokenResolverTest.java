package life.offonoff.ab.web.common.auth;

import life.offonoff.ab.exception.auth.EmptyAuthorizationException;
import life.offonoff.ab.exception.auth.UnsupportedAuthFormatException;
import life.offonoff.ab.exception.auth.token.UnsupportedAuthTokenTypeException;
import life.offonoff.ab.util.token.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationTokenResolverTest {

    @InjectMocks AuthorizationTokenResolver tokenResolver;

    @Mock TokenProvider tokenProvider;

    @Test
    @DisplayName("지원하지 않는 토큰 타입은 예외")
    void resolve_token() {
        // given
        Long memberId = 1L;
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer Token");

        when(tokenProvider.getMemberIdFrom(anyString())).thenReturn(memberId);

        // when
        Long resolved = tokenResolver.resolveToken(request);

        // then
        assertThat(resolved).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Authorization 헤더에 \"\" 는 예외")
    void authorization_header_no_text() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        request.addHeader("Authorization", "");

        // then
        assertThatThrownBy(() -> tokenResolver.resolveToken(request))
                .isInstanceOf(EmptyAuthorizationException.class);
    }

    @Test
    @DisplayName("Authorization 헤더에 값이 없는 경우는 예외")
    void authorization_header_empty() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // then
        assertThatThrownBy(() -> tokenResolver.resolveToken(request))
                .isInstanceOf(EmptyAuthorizationException.class);
    }

    @Test
    @DisplayName("인가 토큰 단어 수가 2개 아니면(1개) 예외")
    void unsupported_format_one_word() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        request.addHeader("Authorization", "BearerToken");

        // then
        assertThatThrownBy(() -> tokenResolver.resolveToken(request))
                .isInstanceOf(UnsupportedAuthFormatException.class);

    }

    @Test
    @DisplayName("인가 토큰 단어 수가 2개 아니면(2개 초과) 예외")
    void unsupported_format_words_over_2() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        request.addHeader("Authorization", "Bearer Token Test");

        // then
        assertThatThrownBy(() -> tokenResolver.resolveToken(request))
                .isInstanceOf(UnsupportedAuthFormatException.class);
    }

    @Test
    @DisplayName("지원하지 않는 토큰 타입은 예외")
    void unsupported_auth_token_type() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        request.addHeader("Authorization", "Basic Token");

        // then
        assertThatThrownBy(() -> tokenResolver.resolveToken(request))
                .isInstanceOf(UnsupportedAuthTokenTypeException.class);

    }
}