package life.offonoff.ab.application.event.topic;

public record VotingResult(
        Long topicId,
        String topicTitle,
        String categoryName,
        String publishMemberName,
        int totalVoteCount
) {
}
