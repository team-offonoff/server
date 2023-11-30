package life.offonoff.ab.exception;

public enum AbCode {
    INVALID_FIELD,
    INVALID_LENGTH_OF_FIELD,

    // Not Found,
    KEYWORD_NOT_FOUND,
    TOPIC_NOT_FOUND,

    // Member
    MEMBER_NOT_FOUND,
    EMAIL_NOT_FOUND,

    UNABLE_TO_VOTE,

    // Mapping
    INVALID_KAKAO_OAUTH_MAPPING,
    ILLEGAL_PASSWORD,
    DUPLICATE_EMAIL,

    // Expired
    EXPIRED_JWT,

    // ILLEGAL STATUS
    ILLEGAL_JOIN_STATUS,
    DUPLICATE_TOPIC_REPORT,
    ;
}
