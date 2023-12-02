package life.offonoff.ab.util.token;

public interface TokenProvider {

    String generateToken(Long memberId);

    Long getMemberIdFrom(String token);
}
