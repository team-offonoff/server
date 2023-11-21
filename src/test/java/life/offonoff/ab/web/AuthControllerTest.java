package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.auth.AuthService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.JoinStatus;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static life.offonoff.ab.web.AuthControllerTest.AuthUri.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AuthControllerTest extends RestDocsTest {

    @MockBean
    AuthService authService;
    ObjectMapper om;

    @BeforeEach
    void beforeEach() {
        om = new ObjectMapper();
    }

    @Test
    @DisplayName("정상 회원가입")
    void signUp() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);
        SignUpResponse response = new SignUpResponse(1L, JoinStatus.AUTH_REGISTERED, "jwt");

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
                                                        "`COMPLETE` : 회원 가입에 필요한 모든 정보가 등록된 상태"),
                                        fieldWithPath("accessToken").description("90일의 생명주기")
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
        SignUpResponse response = new SignUpResponse(1L, JoinStatus.AUTH_REGISTERED, "jwt");

        when(authService.signUp(any(SignUpRequest.class))).thenThrow(DuplicateEmailException.class);

        // then
        mvc.perform(post(BASE + SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.abCode").value(AbCode.DUPLICATE_EMAIL.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("정상 로그인")
    void signIn() throws Exception {
        // given
        String email = "email";
        String password = "password";

        SignInRequest request = new SignInRequest(email, password);
        SignInResponse response = new SignInResponse(1L, JoinStatus.AUTH_REGISTERED, "jwt");

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

        when(authService.signIn(any(SignInRequest.class))).thenThrow(EmailNotFoundException.class);

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

        when(authService.signIn(any(SignInRequest.class))).thenThrow(IllegalPasswordException.class);

        // then
        mvc.perform(post(BASE + SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value(AbCode.ILLEGAL_PASSWORD.name()))
                .andDo(print());
    }

    static class AuthUri {
        public static final String BASE = "/auth";
        public static final String SIGN_UP = "/signup";
        public static final String SIGN_IN = "/signin";
    }
}