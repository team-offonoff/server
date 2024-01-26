package life.offonoff.ab.util.token;

import life.offonoff.ab.exception.auth.token.ExpiredTokenException;
import life.offonoff.ab.exception.auth.token.InvalidSignatureTokenException;
import life.offonoff.ab.exception.auth.token.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TokenProviderTest {

    JwtProvider jwtProvider;

    final String accessKey = "test_access_key_length_64"
            + "25.................."
            + "14............";

    final String refreshKey = "test_refreshkey_length_64"
            + "25.................."
            + "14............";

    final Long expiredIn = 1000L;

    @BeforeEach
    void beforeEach() {
        jwtProvider = new JwtProvider(accessKey, refreshKey, expiredIn, expiredIn);
    }

    @Test
    @DisplayName("정상 decode")
    void parse() {
        // given
        Long memberId = 1L;
        String accessToken = jwtProvider.generateAccessToken(memberId);

        // when
        Long memberIdFrom = jwtProvider.getMemberIdFromAccessToken(accessToken);

        // then
        assertThat(memberIdFrom).isEqualTo(memberId);
    }

    @Test
    @DisplayName("null인 토큰은 예외")
    void decode_null() {

        assertThatThrownBy(() -> jwtProvider.getMemberIdFromAccessToken(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰은 예외")
    void invalid_signature() {
        // given
        Long memberId = 1L;

        // when
        String refreshToken = jwtProvider.generateRefreshToken(memberId);

        // then
        assertThatThrownBy(() -> jwtProvider.getMemberIdFromAccessToken(refreshToken))
                .isInstanceOf(InvalidSignatureTokenException.class);
    }

    @Test
    @DisplayName("만료된 토큰은 예외")
    void expired_token() throws InterruptedException {
        // given
        Long memberId = 1L;
        Long anotherExpiresIn = 1L;
        TokenProvider anotherProvider = new JwtProvider(accessKey, refreshKey, anotherExpiresIn, anotherExpiresIn);


        // when
        String token = anotherProvider.generateAccessToken(memberId);
        Thread.sleep(anotherExpiresIn); // wait expiration

        // then
        assertThatThrownBy(() -> jwtProvider.getMemberIdFromAccessToken(token))
                .isInstanceOf(ExpiredTokenException.class);
    }
}