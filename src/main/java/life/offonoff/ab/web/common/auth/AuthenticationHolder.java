package life.offonoff.ab.web.common.auth;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationHolder {

    private final ThreadLocal<Authentication> threadAuthentication = new ThreadLocal<>();

    public Authentication getAuthentication() {
        Authentication authentication = threadAuthentication.get();

        // Tomcat은 Thread Pool 사용 -> 조회 후 비움.
        threadAuthentication.remove();
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.threadAuthentication.set(authentication);
    }
}
