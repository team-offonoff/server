package life.offonoff.ab.util.password;

public interface PasswordEncoder {

    String encode(String origin);

    boolean isMatch(String origin, String encoded);
}
