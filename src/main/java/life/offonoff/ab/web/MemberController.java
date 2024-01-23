package life.offonoff.ab.web;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.MemberProfileInfoRequest;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@Authorized Long memberId) {
        MemberInfoResponse response = MemberInfoResponse.of(memberService.findById(memberId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/information")
    public ResponseEntity<Void> updateMembersProfileInformation(
            @Authorized final Long memberId,
            final MemberProfileInfoRequest request) {
        memberService.updateMembersProfileInformation(memberId, request);
        return ResponseEntity.ok().build();
    }
}
