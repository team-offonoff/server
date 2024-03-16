package life.offonoff.ab.application.event.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import life.offonoff.ab.web.response.CommentResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentReportEvent implements ReportEvent {
    private final CommentResponse comment;
    private final int reportCount;
    private static final ObjectMapper objectMapper
            = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String getReportedContent() {
        String content;
        try {
            content = objectMapper.writeValueAsString(comment);
        } catch (JsonProcessingException e) {
            content = "commentId: " + comment.getCommentId() + "\ncommentTitle: " + comment.getContent();
        }
        return content;
    }

    @Override
    public int getReportCount() {
        return reportCount;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.COMMENT;
    }
}
