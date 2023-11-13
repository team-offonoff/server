package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.authenticate.OAuthService;
import life.offonoff.ab.application.service.request.auth.OAuthRequest;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.web.response.OAuthSignInResponse;
import life.offonoff.ab.web.response.OAuthSignUpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static life.offonoff.ab.application.service.request.auth.AuthorizeType.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthController.class)
class OAuthControllerTest extends RestDocsTest {

    @MockBean
    OAuthService oAuthService;

    @Test
    void oauth_kakao_new_member_by_code() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_CODE, "authorize_code", "redirect_uri", null);

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignUpResponse(true, "Access Token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(true))
                .andDo(print());
    }

    @Test
    void oauth_kakao_existing_member_by_code() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_CODE, "authorize_code", "redirect_uri", null);

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignInResponse(false, "Access Token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(false))
                .andDo(print());
    }

    @Test
    void oauth_kakao_new_member_by_idToken() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_IDTOKEN, null, null, "id_token");

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignUpResponse(true, "Access Token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(true))
                .andDo(print());
    }

    @Test
    void oauth_kakao_existing_member_by_idToken() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_IDTOKEN, null, null, "id_token");

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignInResponse(false, "Access Token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(false))
                .andDo(print());
    }

    private static class OAuthUri {
        private static final String BASE = "/oauth";
        private static final String KAKAO = "/kakao";
        private static final String AUTHORIZE = "/authorize";
    }
}