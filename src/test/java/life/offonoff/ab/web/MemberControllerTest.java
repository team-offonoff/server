package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.MemberProfileInfoRequest;
import life.offonoff.ab.application.service.request.MemberStatusRequest;
import life.offonoff.ab.application.service.request.ProfileImageRequest;
import life.offonoff.ab.application.service.request.TermsUpdateRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.exception.DuplicateNicknameException;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.exception.NotKoreanEnglishNumberException;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.TermsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
class MemberControllerTest extends RestDocsTest {

    @MockBean
    MemberService memberService;

    @Test
    void updateMembersProfileInformation_withValidField_success() throws Exception {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉네임", "바뀔직업");

        mvc.perform(put(MemberUri.PROFILE_INFO).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMembersProfileInformation_withIllegalLetterNickname_exception() throws Exception {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉!!!", "바뀔직업");

        doThrow(new NotKoreanEnglishNumberException("바뀔닉!!!"))
                .when(memberService).updateMembersProfileInformation(any(), any());

        mvc.perform(put(MemberUri.PROFILE_INFO).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("NOT_KOREAN_ENGLISH_NUMBER"));
    }

    @Test
    void updateMembersProfileInformation_withLongNickname_exception() throws Exception {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("무려8자넘는닉네임", "바뀔직업");

        doThrow(new LengthInvalidException("닉네임", LengthInfo.NICKNAME_LENGTH))
                .when(memberService).updateMembersProfileInformation(any(), any());

        mvc.perform(put(MemberUri.PROFILE_INFO).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("INVALID_LENGTH_OF_FIELD"));
    }

    @Test
    void updateMembersProfileInformation_withDuplicateNickname_exception() throws Exception {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉네임", "바뀔직업");

        doThrow(new DuplicateNicknameException("바뀔닉네임"))
                .when(memberService).updateMembersProfileInformation(any(), any());

        mvc.perform(put(MemberUri.PROFILE_INFO).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("DUPLICATE_NICKNAME"));
    }

    @Test
    void updateMembersProfileImage() throws Exception {
        ProfileImageRequest request = new ProfileImageRequest("htttps://tetestst/test.png");

        mvc.perform(put(MemberUri.PROFILE_IMAGE).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void removeMembersProfileImage() throws Exception {
        mvc.perform(delete(MemberUri.PROFILE_IMAGE).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN"))
                .andExpect(status().isOk());
    }

    @Test
    void getMembersTermsAgreement() throws Exception {
        when(memberService.getMembersTermsAgreement(any()))
                .thenReturn(new TermsResponse(true));

        mvc.perform(get(MemberUri.TERMS)
                            .header("Authorization", "Bearer ACCESS_TOKEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("marketingTermsEnabled").value(true));
    }

    @Test
    void updateMembersTermsAgreement() throws Exception {
        TermsUpdateRequest request = new TermsUpdateRequest(false);
        when(memberService.updateMembersTermsAgreement(any(), any()))
                .thenReturn(new TermsResponse(false));

        mvc.perform(put(MemberUri.TERMS).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("marketingTermsEnabled").value(false));
    }

    @Test
    void updateMembersStatus() throws Exception {
        MemberStatusRequest request = new MemberStatusRequest(false);

        mvc.perform(put(MemberUri.STATUS).with(csrf().asHeader())
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private static class MemberUri {
        private static final String BASE = "/members";
        private static final String PROFILE_INFO = BASE + "/profile/information";
        private static final String PROFILE_IMAGE = BASE + "/profile/image";
        private static final String TERMS = BASE + "/terms";
        private static final String STATUS = BASE + "/status";
    }
}