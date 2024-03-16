package life.offonoff.ab.application.event.report;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Component
public class ReportEventHandler {
    private final Slack slackClient = Slack.getInstance();
    private final String webhookReportUrl;

    public ReportEventHandler(
            @Value("${slack.webhook.report-url}")
            String webhookReportUrl
            ) {
        this.webhookReportUrl = webhookReportUrl;
    }

    private boolean shouldNotifyReport(ReportEvent event) {
        final int notificationStandard = event.getReportType().getNotificationStandard();
        final int reportCount = event.getReportCount();
        return (reportCount > 0)
                // 신고 누적 횟수가 배수가 될 때마다 알림
                && ((reportCount % notificationStandard) != 0);
    }

    @EventListener
    public void sendTopicReportNotification(ReportEvent event) {
        if (!shouldNotifyReport(event)) {
            return;
        }
        sendSlackAlertErrorLog(generateSlackAttachment(event.getReportType().name(), event));
    }

    private void sendSlackAlertErrorLog(Attachment attachment) {
        try {
            slackClient.send(
                    webhookReportUrl,
                    payload(p -> p.text("신고 누적!").attachments(List.of(attachment))));
        } catch (IOException e) {
            log.debug("Slack 신고 누적 알림 전송 실패");
        }
    }

    // attachment 생성 메서드
    private Attachment generateSlackAttachment(String title, ReportEvent event) {
        return Attachment.builder()
                .color("ff0000")  // 붉은 색
                .title("🚨"+title+" REPORT🚨")
                .fields(List.of(
                                generateSlackField("신고 횟수", String.valueOf(event.getReportCount())),
                                generateSlackField("내용", event.getReportedContent())
                        )
                )
                .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }
}
