package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.auth.OAuthService;
import life.offonoff.ab.application.service.request.oauth.OAuthRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.member.JoinStatus;
import life.offonoff.ab.exception.IllegalJoinStatusException;
import life.offonoff.ab.exception.MemberDeactivatedException;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.oauth.OAuthSignInResponse;
import life.offonoff.ab.web.response.oauth.OAuthSignUpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static life.offonoff.ab.application.service.request.oauth.AuthorizeType.BY_CODE;
import static life.offonoff.ab.application.service.request.oauth.AuthorizeType.BY_IDTOKEN;
import static life.offonoff.ab.domain.member.JoinStatus.AUTH_REGISTERED;
import static life.offonoff.ab.domain.member.JoinStatus.COMPLETE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = OAuthController.class,
            excludeFilters = {
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
            })
class OAuthControllerTest extends RestDocsTest {

    @MockBean
    OAuthService oAuthService;

    @Test
    void oauth_kakao_new_member_by_code() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_CODE, "authorize_code", "redirect_uri", null);

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignUpResponse(true, 1L, AUTH_REGISTERED));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(true))
                .andDo(restDocs.document(
                        relaxedRequestFields(
                                fieldWithPath("type")
                                        .description(" !필수! `인가코드&리다이렉트` 로 인가하는 방식과 `id_token`으로 인가하는 방식 두가지를 운영... 이를 구분하기 위한 type"),
                                fieldWithPath("code").optional()
                                        .description("카카오 서버로 부터 받은 authorize code, redirect_uri와 필요"),
                                fieldWithPath("redirect_uri").optional()
                                        .description("authorize code를 받을 때 입력했던 redirect_uri"),
                                fieldWithPath("id_token")
                                        .type(String.class)
                                        .description("카카오 서버로 부터 받은 id_token").optional(),
                                fieldWithPath("provider").description("서버 내 작업 용. 입력 X").optional()
                        )))
                .andDo(restDocs.document(
                        relaxedResponseFields(
                                fieldWithPath("joinStatus").type(JoinStatus.class)
                                        .description("AUTH와 동일")
                        )
                ));
    }

    @Test
    void oauth_kakao_existing_member_by_code() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_CODE, "authorize_code", "redirect_uri", null);

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignInResponse(false, 1L, COMPLETE, "access_token", "refresh_token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(false))
                .andDo(print());
    }

    @Test
    void oauth_kakao_deactivated_member_by_code() throws Exception {
        OAuthRequest request = new OAuthRequest(BY_CODE, "authorize_code", "redirect_uri", null);

        when(oAuthService.authorize(any()))
                .thenThrow(MemberDeactivatedException.class);

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.abCode").value("DEACTIVATED_MEMBER"))
                .andDo(print());
    }

    @Test
    void oauth_kakao_new_member_by_idToken() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_IDTOKEN, null, null, "id_token");

        when(oAuthService.authorize(any()))
                .thenReturn(new OAuthSignUpResponse(true, 1L, AUTH_REGISTERED));

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
                .thenReturn(new OAuthSignInResponse(false, 1L, COMPLETE, "access_token", "refresh_token"));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newMember").value(false))
                .andDo(print());
    }

    @Test
    void oauth_kakao_not_completety_joined_member_by_idToken() throws Exception {
        // given
        OAuthRequest request = new OAuthRequest(BY_IDTOKEN, null, null, "id_token");

        when(oAuthService.authorize(any()))
                .thenThrow(new IllegalJoinStatusException(1L, AUTH_REGISTERED));

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void oauth_kakao_deactivated_member_by_idToken() throws Exception {
        OAuthRequest request = new OAuthRequest(BY_IDTOKEN, null, null, "id_token");

        when(oAuthService.authorize(any()))
                .thenThrow(MemberDeactivatedException.class);

        mvc.perform(post(OAuthUri.BASE + OAuthUri.KAKAO + OAuthUri.AUTHORIZE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.abCode").value("DEACTIVATED_MEMBER"))
                .andDo(print());
    }

    private static class OAuthUri {
        private static final String BASE = "/oauth";
        private static final String KAKAO = "/kakao";
        private static final String GOOGLE = "google";
        private static final String AUTHORIZE = "/authorize";
    }
}