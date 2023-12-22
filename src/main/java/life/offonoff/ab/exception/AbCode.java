package life.offonoff.ab.exception;

public enum AbCode {
    INVALID_FIELD,
    INVALID_LENGTH_OF_FIELD,

    // Not Found,
    KEYWORD_NOT_FOUND,
    TOPIC_NOT_FOUND,
    COMMENT_NOT_FOUND,

    // Member
    MEMBER_NOT_FOUND,
    EMAIL_NOT_FOUND,

    UNABLE_TO_VOTE,
    VOTED_BY_AUTHOR,
    MEMBER_NOT_VOTE,

    // Topic
    ILLEGAL_AUTHOR,

    // Vote
    DUPLICATE_VOTE,

    // COMMENT
    UNABLE_TO_VIEW_COMMENTS,

    // Mapping
    INVALID_KAKAO_OAUTH_MAPPING,
    ILLEGAL_PASSWORD,
    DUPLICATE_EMAIL,

    // Expired


    // ILLEGAL STATUS
    ILLEGAL_JOIN_STATUS,
    DUPLICATE_TOPIC_REPORT,
    ILLEGAL_TOPIC_STATUS_CHANGE,
    ILLEGAL_COMMENT_STATUS_CHANGE,
    ILLEGAL_FILE_EXTENSION,

    // Auth
    UNSUPPORTED_AUTH_FORMAT,
    EMPTY_AUTHORIZATION,

    // Token
    INVALID_TOKEN,
    EXPIRED_TOKEN,
    INVALID_SIGNATURE_TOKEN,

    FUTURE_TIME_REQUEST;
}
