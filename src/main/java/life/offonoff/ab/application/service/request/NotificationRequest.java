package life.offonoff.ab.application.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String receiver;

    public static NotificationRequest empty() {
        return new NotificationRequest();
    }
}
