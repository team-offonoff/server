package life.offonoff.ab.application.service.member;

import life.offonoff.ab.application.service.S3Service;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.application.service.request.MemberProfileInfoRequest;
import life.offonoff.ab.application.service.request.MemberRequest;
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
    public Member findById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId)); // custom exception 추가 후 예외 핸들
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberByEmailNotFoundException(email));
    }

    //== exists ==//
    public boolean existsById(final Long memberId) {
        try {
            findById(memberId);
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

        Member member = findById(memberId);
        member.updateNickname(request.nickname());
        member.updateJob(request.job());
    }

    public void checkMembersNickname(String nickname) {
        int length = TextUtils.countGraphemeClusters(nickname);
        if (length < LengthInfo.NICKNAME_LENGTH.getMinLength() || length > LengthInfo.NICKNAME_LENGTH.getMaxLength()) {
            throw new LengthInvalidException("닉네임", LengthInfo.NICKNAME_LENGTH);
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
    public void updateMembersProfileImage(Long memberId, String imageUrl) {
        Member member = findById(memberId);
        removeMembersProfileImage(member);

        member.updateProfileImageUrl(imageUrl);
    }

    @Transactional
    public void removeMembersProfileImage(Long memberId) {
        Member member = findById(memberId);
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
        Member member = findById(memberId);
        member.agreeTerms(request.toTermsEnabled());
        return TermsResponse.from(member.getTermsEnabled());
    }

    public TermsResponse getMembersTermsAgreement(Long memberId) {
        Member member = findById(memberId);
        return TermsResponse.from(member.getTermsEnabled());
    }
}
