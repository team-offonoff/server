package life.offonoff.ab.application.event.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TopicReportEvent implements ReportEvent{
    private final TopicResponse topic;
    private final int reportCount;
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String getReportedContent() {
        String content;
        try {
            content = objectMapper.writeValueAsString(topic);
        } catch (JsonProcessingException e) {
            content = "topicId: "+topic.topicId() + "\ntopicTitle: "+topic.topicTitle();
        }
        return content;
    }

    @Override
    public int getReportCount() {
        return reportCount;
    }
}
