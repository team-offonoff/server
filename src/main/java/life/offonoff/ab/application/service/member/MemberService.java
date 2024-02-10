package life.offonoff.ab.application.service.member;

import life.offonoff.ab.application.service.S3Service;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.application.service.request.MemberProfileInfoRequest;
import life.offonoff.ab.application.service.request.MemberRequest;
import life.offonoff.ab.application.service.request.ProfileImageResponse;
import life.offonoff.ab.application.service.request.TermsUpdateRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.web.response.TermsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    //== join ==//
    @Transactional
    public Member join(final MemberRequest request) {
        return memberRepository.save(request.toMember());
    }

    //== find ==//
    public Member findMemberIncludeDeactivated(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId)); // custom exception 추가 후 예외 핸들
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberByEmailNotFoundException(email));
    }

    public Member findMember(final Long memberId) {
        Member member = findMemberIncludeDeactivated(memberId);
        if (!member.isActive()) {
            throw new MemberDeactivatedException(memberId);
        }
        return member;
    }

    public Member findMember(final String email) {
        Member member = findByEmail(email);
        if (!member.isActive()) {
            throw new MemberDeactivatedException(email);
        }
        return member;
    }


    //== exists ==//
    public boolean existsById(final Long memberId) {
        try {
            findMemberIncludeDeactivated(memberId);
        } catch (MemberNotFoundException notFountException) {
            return false;
        }
        return true;
    }

    public boolean existsByEmail(final String email) {
        try {
            findByEmail(email);
        } catch (MemberNotFoundException notFountException) {
            return false;
        }
        return true;
    }

    @Transactional
    public void updateMembersProfileInformation(final Long memberId, final MemberProfileInfoRequest request) {
        checkMembersNickname(request.nickname());
        checkMembersJob(request.job());

        Member member = findMember(memberId);
        member.updateNickname(request.nickname());
        member.updateJob(request.job());
    }

    public void checkMembersNickname(String nickname) {
        int length = TextUtils.countGraphemeClusters(nickname);
        if (length < LengthInfo.NICKNAME.getMinLength() || length > LengthInfo.NICKNAME.getMaxLength()) {
            throw new LengthInvalidException("닉네임", LengthInfo.NICKNAME);
        }

        if (!TextUtils.isOnlyKoreanEnglishNumberIncluded(nickname)) {
            throw new NotKoreanEnglishNumberException(nickname);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException(nickname);
        }
    }

    public void checkMembersJob(String job) {
        int length = TextUtils.countGraphemeClusters(job);
        if (length < LengthInfo.JOB_LENGTH.getMinLength() || length > LengthInfo.JOB_LENGTH.getMaxLength()) {
            throw new LengthInvalidException("직업", LengthInfo.JOB_LENGTH);
        }

        if (!TextUtils.isOnlyKoreanEnglishNumberIncluded(job)) {
            throw new NotKoreanEnglishNumberException(job);
        }
    }

    @Transactional
    public ProfileImageResponse updateMembersProfileImage(Long memberId, String imageUrl) {
        Member member = findMember(memberId);
        removeMembersProfileImage(member);

        String updatedUrl = member.updateProfileImageUrl(imageUrl);
        return new ProfileImageResponse(updatedUrl);
    }

    @Transactional
    public void removeMembersProfileImage(Long memberId) {
        Member member = findMember(memberId);
        removeMembersProfileImage(member);
    }

    private void removeMembersProfileImage(Member member) {
        String originalUrl = member.getProfileImageUrl();
        if (originalUrl != null) {
            s3Service.deleteFile(originalUrl);
        }
    }

    @Transactional
    public TermsResponse updateMembersTermsAgreement(final Long memberId, final TermsUpdateRequest request) {
        Member member = findMember(memberId);
        member.agreeTerms(request.toTermsEnabled());
        return TermsResponse.from(member.getTermsEnabled());
    }

    public TermsResponse getMembersTermsAgreement(Long memberId) {
        Member member = findMember(memberId);
        return TermsResponse.from(member.getTermsEnabled());
    }

    @Transactional
    public void activateMember(final Long memberId, final boolean activated) {
        Member member = findMember(memberId);
        member.activate(activated);
    }
}
