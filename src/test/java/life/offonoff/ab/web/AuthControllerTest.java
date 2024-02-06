package life.offonoff.ab.web;

import life.offonoff.ab.application.service.auth.AuthService;
import life.offonoff.ab.application.service.request.TermsRequest;
import life.offonoff.ab.application.service.request.auth.ProfileRegisterRequest;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.member.Gender;
import life.offonoff.ab.domain.member.JoinStatus;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.exception.auth.token.ExpiredTokenException;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.TokenRequest;
import life.offonoff.ab.web.response.TokenResponse;
import life.offonoff.ab.web.response.auth.join.ProfileRegisterResponse;
import life.offonoff.ab.web.response.auth.join.SignUpResponse;
import life.offonoff.ab.web.response.auth.join.JoinTermsResponse;
import life.offonoff.ab.web.response.auth.login.SignInResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static life.offonoff.ab.web.AuthControllerTest.AuthUri.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
class AuthControllerTest extends RestDocsTest {

    @MockBean
    AuthService authService;

    @Test
    @DisplayName("정상 회원가입")
    void signUp() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);
        SignUpResponse response = new SignUpResponse(1L, JoinStatus.AUTH_REGISTERED);

        when(authService.signUp(any(SignUpRequest.class))).thenReturn(response);

        // then
        mvc.perform(post(BASE + SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.memberId").value(response.getMemberId()))
                .andDo(restDocs
                        .document(
                                relaxedResponseFields(
                                        fieldWithPath("joinStatus").type(JoinStatus.class)
                                                .description("" +
                                                        "`EMPTY` : 인증 정보 (이메일 / 비번 등)가 등록되지 않은 상태, 인증정보 등록 필요 \n " +
                                                        "`AUTH_REGISTERED` : 인증 정보가 등록된 상태, 개인 정보 등록 필요 \n " +
                                                        "`PERSONAL_REGISTERED` : 개인 정보가 등록된 상태, 약관 동의 필요\n " +
                                                        "`COMPLETE` : 회원 가입에 필요한 모든 정보가 등록된 상태")
                                        //, fieldWithPath("accessToken").description("90일의 생명주기")
                                )
                        ));
    }

    @Test
    @DisplayName("회원가입 시 중복 이메일은 예외")
    void signUp_exception_duplicate_email() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);

        when(authService.signUp(any(SignUpRequest.class))).thenThrow(new DuplicateEmailException(email));

        // then
        mvc.perform(post(BASE + SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.abCode").value(AbCode.DUPLICATE_EMAIL.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("계정 등록 후에는 개인 정보 등록")
    void signup_profile() throws Exception {
        // given
        Long memberId = 1L;
        LocalDate birth = LocalDate.of(2023, 11, 28);
        ProfileRegisterRequest request = new ProfileRegisterRequest(memberId,
                                                           "nickname",
                                                                    birth,
                                                                    Gender.MALE,
                                                                    "job");
        ProfileRegisterResponse response = new ProfileRegisterResponse(memberId, JoinStatus.PERSONAL_REGISTERED);

        when(authService.registerProfile(any(ProfileRegisterRequest.class))).thenReturn(response);

        // then
        mvc.perform(post(BASE + SIGN_UP + PROFILE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.memberId").value(response.getMemberId()))
                .andDo(print());
    }

    @Test
    @DisplayName("개인 정보 중복 등록시 예외X 기존 내용대로 유지")
    void signup_profile_exception() throws Exception {
        Long memberId = 1L;
        LocalDate birth = LocalDate.of(2023, 11, 28);
        ProfileRegisterRequest request = new ProfileRegisterRequest(memberId,
                                                                    "nickname",
                                                                    birth,
                                                                    Gender.MALE,
                                                                    "job");
        ProfileRegisterResponse response = new ProfileRegisterResponse(memberId, JoinStatus.PERSONAL_REGISTERED);

        when(authService.registerProfile(any(ProfileRegisterRequest.class))).thenReturn(response);

        // then
        mvc.perform(post(BASE + SIGN_UP + PROFILE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.memberId").value(response.getMemberId()))
                .andDo(print());
    }

    @Test
    @DisplayName("개인 정보 등록 후엔 약관 동의 -> access token 발급")
    void enable_terms() throws Exception {
        // given
        Long memberId = 1L;

        TermsRequest request = new TermsRequest(memberId, true);
        JoinTermsResponse response = new JoinTermsResponse(1L, JoinStatus.COMPLETE, "access_token", "refresh_token");

        when(authService.registerTerms(any(TermsRequest.class))).thenReturn(response);

        // when
        mvc.perform(post(BASE + SIGN_UP + TERMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("중복 약관 동의는 예외X 기존 내용대로 유지")
    void enable_terms_exception() throws Exception {
        // given
        Long memberId = 1L;

        TermsRequest request = new TermsRequest(memberId, true);
        JoinTermsResponse response = new JoinTermsResponse(1L, JoinStatus.COMPLETE, "access_token", "refresh_token");

        when(authService.registerTerms(any(TermsRequest.class))).thenReturn(response);

        // when
        mvc.perform(post(BASE + SIGN_UP + TERMS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("정상 로그인")
    void signIn() throws Exception {
        // given
        String email = "email";
        String password = "password";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        SignInRequest request = new SignInRequest(email, password);
        SignInResponse response = new SignInResponse(1L, JoinStatus.AUTH_REGISTERED, accessToken, refreshToken);

        when(authService.signIn(any(SignInRequest.class))).thenReturn(response);

        // then
        mvc.perform(post(BASE + SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(jsonPath("$.memberId").value(response.getMemberId()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 시 존재하지 않는 이메일은 예외")
    void signIn_exception_email_not_found() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignInRequest request = new SignInRequest(email, password);

        when(authService.signIn(any(SignInRequest.class))).thenThrow(new EmailNotFoundException(email));

        // then
        mvc.perform(post(BASE + SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(status().isNotFound(),
                        jsonPath("abCode").value(AbCode.EMAIL_NOT_FOUND.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 시 틀린 비밀번호는 예외")
    void signIn_exception_wrong_password() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignInRequest request = new SignInRequest(email, password);

        when(authService.signIn(any(SignInRequest.class))).thenThrow(new IllegalPasswordException());

        // then
        mvc.perform(post(BASE + SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value(AbCode.ILLEGAL_PASSWORD.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("인증 토큰 재발급")
    void get_auth_tokens() throws Exception {
        // given
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";

        TokenRequest request = new TokenRequest("old_refresh_token");
        TokenResponse expected = TokenResponse.builder()
                                              .memberId(1L)
                                              .accessToken(newAccessToken)
                                              .refreshToken(newRefreshToken)
                                              .build();

        when(authService.getAuthTokens(any())).thenReturn(expected);

        // then
        mvc.perform(get(BASE + TOKENS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.memberId").value(expected.getMemberId()),
                        jsonPath("$.accessToken").value(expected.getAccessToken()),
                        jsonPath("$.refreshToken").value(expected.getRefreshToken()))
                .andDo(print());
    }

    @Test
    @DisplayName("만료된 refresh_token으로 재발급은 예외")
    void get_auth_tokens_exception_expired_refresh_token() throws Exception {
        // given
        TokenRequest request = new TokenRequest("expired_refresh_token");

        when(authService.getAuthTokens(any())).thenThrow(ExpiredTokenException.class);

        // then
        mvc.perform(get(BASE + TOKENS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(
                        status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("비활성화된 Member의 refresh_token으로 재발급은 예외")
    void get_auth_tokens_exception_deactivated_member() throws Exception {
        // given
        TokenRequest request = new TokenRequest("old_refresh_token");

        when(authService.getAuthTokens(any())).thenThrow(MemberDeactivatedException.class);

        // then
        mvc.perform(get(BASE + TOKENS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 Member의 refresh_token으로 재발급은 예외")
    void get_auth_tokens_exception_not_found_member() throws Exception {
        // given
        TokenRequest request = new TokenRequest("old_refresh_token");

        when(authService.getAuthTokens(any())).thenThrow(MemberByIdNotFoundException.class);

        // then
        mvc.perform(get(BASE + TOKENS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(
                        status().isNotFound())
                .andDo(print());
    }

    static class AuthUri {
        public static final String BASE = "/auth";
        public static final String SIGN_UP = "/signup";
        public static final String JOIN_STATUS = "/status";
        public static final String PROFILE = "/profile";
        public static final String TERMS = "/terms";
        public static final String SIGN_IN = "/signin";
        public static final String TOKENS = "/tokens";
    }
}