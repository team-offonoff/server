package life.offonoff.ab.application.event.report;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
public enum ReportType {
    TOPIC,
    COMMENT
    ;

    private int notificationStandard;

    @Component
    private static class NotificationStandardInjector {
        private static final int DEFAULT = 5;

        @Autowired
        public NotificationStandardInjector(
                @Value("${slack.standard.topic-report}") Integer topicStandard,
                @Value("${slack.standard.comment-report}") Integer commentStandard
        ) {
            TOPIC.notificationStandard = getOrDefault(topicStandard);
            COMMENT.notificationStandard = getOrDefault(commentStandard);
        }

        private int getOrDefault(Integer standard) {
            return (standard != null && standard > 0) ? standard : DEFAULT;
        }
    }
}
