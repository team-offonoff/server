package life.offonoff.ab.util.token;

public interface TokenProvider {

    String generateAccessToken(Long memberId);
    String generateRefreshToken(Long memberId);

    Long getMemberIdFromAccessToken(String token);
    Long getMemberIdFromRefreshToken(String token);

}
