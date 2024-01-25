package life.offonoff.ab.application.service.member;

import life.offonoff.ab.application.service.request.MemberProfileInfoRequest;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.DuplicateNicknameException;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.exception.MemberDeactivatedException;
import life.offonoff.ab.exception.NotKoreanEnglishNumberException;
import life.offonoff.ab.repository.member.MemberRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    void updateMembersProfileInformation_withValidFields_success() {
        // given
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉네임", "바뀔직업");
        Member member = TestEntityUtil.TestMember.builder()
                .id(1L)
                .nickname("닉네임")
                .job("직업")
                .build()
                .buildMember();
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        // when
        memberService.updateMembersProfileInformation(1L, request);

        // then
        assertThat(member.getPersonalInfo().getJob()).isEqualTo("바뀔직업");
        assertThat(member.getPersonalInfo().getNickname()).isEqualTo("바뀔닉네임");
    }

    @Test
    void updateMembersProfileInformation_withIllegalLetterNickname_exception() {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉!!!", "바뀔직업");

        ThrowingCallable code = () ->
                memberService.updateMembersProfileInformation(1L, request);

        assertThatThrownBy(code)
                .isInstanceOf(NotKoreanEnglishNumberException.class);
    }

    @Test
    void updateMembersProfileInformation_withLongNickname_exception() {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("무려8자넘는닉네임", "바뀔직업");

        ThrowingCallable code = () ->
                memberService.updateMembersProfileInformation(1L, request);

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    void updateMembersProfileInformation_withDuplicateNickname_exception() {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("닉네임", "바뀔직업");
        when(memberRepository.existsByNickname(any())).thenReturn(true);

        ThrowingCallable code = () ->
                memberService.updateMembersProfileInformation(1L, request);

        assertThatThrownBy(code)
                .isInstanceOf(DuplicateNicknameException.class);
    }

    @Test
    void updateMembersProfileInformation_withIllegalLetterJob_exception() {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉네임", "바뀔직업!");

        ThrowingCallable code = () ->
                memberService.updateMembersProfileInformation(1L, request);

        assertThatThrownBy(code)
                .isInstanceOf(NotKoreanEnglishNumberException.class);
    }

    @Test
    void updateMembersProfileInformation_withLongJob_exception() {
        MemberProfileInfoRequest request = new MemberProfileInfoRequest("바뀔닉네임", "무려12자가넘는직업이라니");

        ThrowingCallable code = () ->
                memberService.updateMembersProfileInformation(1L, request);

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    void activateMember_deactivate() {
        // given
        Member member = TestEntityUtil.TestMember.builder()
                .id(1L)
                .nickname("닉네임")
                .job("직업")
                .build()
                .buildMember();
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        // when
        memberService.activateMember(1L, false);

        // then
        assertThat(member.isActive()).isFalse();
    }

    @Test
    void findMember_deactivatedMember_exception() {
        Member member = TestEntityUtil.TestMember.builder()
                .id(1L)
                .nickname("닉네임")
                .job("직업")
                .build()
                .buildMember();
        member.activate(false);
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        ThrowingCallable code = () ->
                memberService.findMember(1L);

        assertThatThrownBy(code)
                .isInstanceOf(MemberDeactivatedException.class);
    }
}