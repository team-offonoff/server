package life.offonoff.ab.web;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.*;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.response.MembersProfileResponse;
import life.offonoff.ab.web.response.TermsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<MembersProfileResponse> getMembersProfile(
            @Authorized final Long memberId
    ) {
        MembersProfileResponse response = MembersProfileResponse.from(memberService.findMember(memberId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/information")
    public ResponseEntity<Void> updateMembersProfileInformation(
            @Authorized final Long memberId,
            @RequestBody final MemberProfileInfoRequest request) {
        memberService.updateMembersProfileInformation(memberId, request);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @PutMapping("/profile/image")
    public ResponseEntity<ProfileImageResponse> updateMembersProfileImage(
            @Authorized final Long memberId,
            @RequestBody final ProfileImageRequest request
        ) {
        ProfileImageResponse response = memberService.updateMembersProfileImage(memberId, request.imageUrl());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile/image")
    public ResponseEntity<Void> removeMembersProfileImage(
            @Authorized final Long memberId
    ) {
        memberService.removeMembersProfileImage(memberId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/terms")
    public ResponseEntity<TermsResponse> getMembersTermsAgreement(@Authorized Long memberId) {
        return ResponseEntity.ok(memberService.getMembersTermsAgreement(memberId));
    }

    @PutMapping("/terms")
    public ResponseEntity<TermsResponse> updateMembersTermsAgreement(
            @Authorized Long memberId,
            @RequestBody final TermsUpdateRequest request
    ) {
        TermsResponse response = memberService.updateMembersTermsAgreement(memberId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateMembersStatus(
            @Authorized Long memberId,
            @RequestBody final MemberStatusRequest request
    ) {
        memberService.activateMember(memberId, request.activated());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }
}
