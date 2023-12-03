package life.offonoff.ab.util.token;

import life.offonoff.ab.exception.auth.token.ExpiredTokenException;
import life.offonoff.ab.exception.auth.token.InvalidSignatureTokenException;
import life.offonoff.ab.exception.auth.token.InvalidTokenException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    JwtProvider jwtProvider;

    final String secretKey = "test_secret_key_length_64"
            + "25.................."
            + "14............";

    final String anotherSecretKey = "test_another_secret_key_length_64"
            + "25.................."
            + "6.....";

    final Long expiredIn = 1000L;

    @BeforeEach
    void beforeEach() {
        jwtProvider = new JwtProvider(secretKey, expiredIn);
    }

    @Test
    @DisplayName("정상 decode")
    void parse() {
        // given
        Long memberId = 1L;
        String accessToken = jwtProvider.generateToken(memberId);

        // when
        Long memberIdFrom = jwtProvider.getMemberIdFrom(accessToken);

        // then
        assertThat(memberIdFrom).isEqualTo(memberId);
    }

    @Test
    @DisplayName("null인 토큰은 예외")
    void decode_null() {

        assertThatThrownBy(() -> jwtProvider.getMemberIdFrom(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰은 예외")
    void invalid_signature() {
        // given
        Long memberId = 1L;
        TokenProvider anotherProvider = new JwtProvider(anotherSecretKey, expiredIn);

        // when
        String anotherToken = anotherProvider.generateToken(memberId);

        // then
        assertThatThrownBy(() -> jwtProvider.getMemberIdFrom(anotherToken))
                .isInstanceOf(InvalidSignatureTokenException.class);

    }

    @Test
    @DisplayName("만료된 토큰은 예외")
    void expired_token() throws InterruptedException {
        // given
        Long memberId = 1L;
        Long anotherExpiresIn = 1L;
        TokenProvider anotherProvider = new JwtProvider(secretKey, anotherExpiresIn);

        // when
        String token = anotherProvider.generateToken(memberId);
        Thread.sleep(anotherExpiresIn); // wait expiration

        // then
        assertThatThrownBy(() -> jwtProvider.getMemberIdFrom(token))
                .isInstanceOf(ExpiredTokenException.class);
    }
}