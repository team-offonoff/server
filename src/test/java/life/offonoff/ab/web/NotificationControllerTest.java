package life.offonoff.ab.web;

import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import life.offonoff.ab.web.response.notification.message.CommentOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteResultNotificationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;

import static life.offonoff.ab.domain.notification.NotificationType.*;
import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.COMMENT_ON_TOPIC_TITLE;
import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.VOTE_COUNT_ON_TOPIC_TITLE;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = NotificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
class NotificationControllerTest extends RestDocsTest {

    @MockBean
    NotificationService notificationService;

    @Test
    void get_members_notifications() throws Exception {
        // given
        NotificationResponse voteResultResponse = new NotificationResponse(
                VOTE_RESULT_NOTIFICATION,
                false,
                new VoteResultNotificationMessage("test_title1", 1L));

        NotificationResponse voteCountResponse = new NotificationResponse(
                VOTE_COUNT_ON_TOPIC_NOTIFICATION,
                false,
                new VoteCountOnTopicNotificationMessage(VOTE_COUNT_ON_TOPIC_TITLE.formatted(100), "test_title2", 2L));

        NotificationResponse commentOnTopicResponse = new NotificationResponse(
                COMMENT_ON_TOPIC_NOTIFICATION,
                false,
                new CommentOnTopicNotificationMessage(COMMENT_ON_TOPIC_TITLE, "test_title3", 3L, 1L)
        );

        when(notificationService.findAllByReceiverId(nullable(Long.class)))
                .thenReturn(List.of(voteResultResponse, voteCountResponse, commentOnTopicResponse));

        // then
        mvc.perform(get(NoticeUri.BASE)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    private static class NoticeUri {
        public static String BASE = "/notifications";
    }
}