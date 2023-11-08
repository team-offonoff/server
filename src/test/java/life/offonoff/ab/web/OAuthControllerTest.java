package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.authenticate.OAuthService;
import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.web.response.OAuthSignUpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OAuthController.class)
class OAuthControllerTest extends RestDocsTest {

    @MockBean
    OAuthService oAuthService;

    @Test
    void oauth_kakao_new_member() throws Exception {
        // given
        KakaoAuthRequest request = new KakaoAuthRequest("authorize_code", "redirect_uri");

        when(oAuthService.authenticate(any()))
                .thenReturn(new OAuthSignUpResponse(true, "Access Token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(true))
                .andDo(print());
    }

    private static class OAuthUri {
        private static final String BASE = "/oauth";
        private static final String KAKAO = "/kakao";
        private static final String AUTHORIZE = "/authorize";
    }
}