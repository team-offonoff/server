package life.offonoff.ab.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.auth.Authentication;
import life.offonoff.ab.web.common.auth.AuthenticationHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthenticationHolder authenticationHolder;
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    private static final String BEARER_PREFIX = "bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("authentication filter");
        if (needAuthentication(request)) {
            String accessToken = getAccessToken(request);
            log.info("accecc_token : {}", accessToken);

            Authentication authentication = getAuthentication(accessToken);
            // ThreadLocal에 인증 정보 저장
            authenticationHolder.setAuthentication(authentication);
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private Authentication getAuthentication(String accessToken) {
        Long memberId = jwtProvider.parseMemberId(accessToken);
        Member member = memberService.find(memberId);

        log.info("(member-id, role) : ({}, {})", member.getId(), member.getRole());
        return new Authentication(member.getId(), member.getRole());
    }

    private String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization.startsWith(BEARER_PREFIX)) {
            return authorization.split(" ")[1];
        }

        throw new RuntimeException();
    }

    private boolean needAuthentication(HttpServletRequest request) {
        // TODO 인증 필요한 URI 관리 로직
        return false;
    }
}
