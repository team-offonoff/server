package life.offonoff.ab.web.response;

public record VoteResponse(CommentResponse latestComment) {
    public static VoteResponse from(CommentResponse comment) {
        return new VoteResponse(comment);
    }
}
