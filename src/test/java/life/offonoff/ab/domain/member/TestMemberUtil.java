package life.offonoff.ab.domain.member;

public class TestMemberUtil {

    public static Member createAdminMember(String email, String password) {
        AuthenticationInfo authInfo = new AuthenticationInfo(email, password, Provider.NONE);
        authInfo.setRole(Role.ADMIN);

        Member member = new Member();
        member.setAuthInfo(authInfo);
        member.setNotificationEnabled(NotificationEnabled.allEnabled());
        return member;
    }

}