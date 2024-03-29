package life.offonoff.ab.application.service.auth.oauth.rest;

import life.offonoff.ab.domain.member.Provider;

public class OAuthRestRequestConst {

    // GOOGLE
    public final static String POST_GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    // KAKAO
    public final static String POST_KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    // email / nickname 확인 위해 id_token decode 말고 직접 조회도 있음
    public final static String GET_KAKAO_USER_INFO = "https://kapi.kakao.com/v1/oidc/userinfo";

    public static String findPostTokenUrlOf(Provider provider) {

        if (provider == Provider.KAKAO) {
            return POST_KAKAO_TOKEN_URL;
        }

        if (provider == Provider.GOOGLE) {
            return POST_GOOGLE_TOKEN_URL;
        }

        throw new RuntimeException();
    }
}
