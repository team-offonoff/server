package life.offonoff.ab.web;

import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.notification.ReceiverType;
import life.offonoff.ab.exception.IllegalReceiverException;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import life.offonoff.ab.web.response.notification.message.CommentOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.LikeInCommentNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteResultNotificationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;

import static life.offonoff.ab.domain.notification.NotificationType.*;
import static life.offonoff.ab.domain.notification.ReceiverType.AUTHOR;
import static life.offonoff.ab.domain.notification.ReceiverType.VOTER;
import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
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
    void get_members_all_notifications() throws Exception {
        // given
        NotificationResponse voteResultResponse = new NotificationResponse(
                1L,
                VOTE_RESULT_NOTIFICATION,
                VOTER,
                false,
                new VoteResultNotificationMessage("test_title1", 1L));

        NotificationResponse voteCountResponse = new NotificationResponse(
                2L,
                VOTE_COUNT_ON_TOPIC_NOTIFICATION,
                AUTHOR,
                false,
                new VoteCountOnTopicNotificationMessage(VOTE_COUNT_ON_TOPIC_TITLE.formatted(100), "test_title2", 2L));

        NotificationResponse commentOnTopicResponse = new NotificationResponse(
                3L,
                COMMENT_ON_TOPIC_NOTIFICATION,
                AUTHOR,
                false,
                new CommentOnTopicNotificationMessage(COMMENT_ON_TOPIC_TITLE, "test_title3", 3L, 1L)
        );

        NotificationResponse likeInCommentResponse = new NotificationResponse(
                4L,
                LIKE_IN_COMMENT_NOTIFICATION,
                VOTER,
                false,
                new LikeInCommentNotificationMessage(LIKE_ON_COMMENT_TITLE, "test_title4", 4L, 2L)
        );

        when(notificationService.findAllByReceiverId(any(), any()))
                .thenReturn(List.of(voteResultResponse, voteCountResponse, commentOnTopicResponse, likeInCommentResponse));

        // then
        mvc.perform(get(NotificationUri.BASE)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    @Test
    void get_members_notifications_receiver_VOTER() throws Exception {
        // given
        NotificationResponse voteResultResponse = new NotificationResponse(
                1L,
                VOTE_RESULT_NOTIFICATION,
                VOTER,
                false,
                new VoteResultNotificationMessage("test_title1", 1L));

        NotificationResponse likeInCommentResponse = new NotificationResponse(
                2L,
                LIKE_IN_COMMENT_NOTIFICATION,
                VOTER,
                false,
                new LikeInCommentNotificationMessage(LIKE_ON_COMMENT_TITLE, "test_title4", 4L, 2L)
        );

        when(notificationService.findAllByReceiverId(any(), any()))
                .thenReturn(List.of(voteResultResponse, likeInCommentResponse));

        // then
        mvc.perform(get(NotificationUri.RECEIVER_PARAM, "VOTER")
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    @Test
    void read_notification_by_receiver() throws Exception {
        mvc.perform(post(NotificationUri.READ, 1L)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    @Test
    void read_notification_exception_by_non_receiver() throws Exception {
        doThrow(new IllegalReceiverException(1L, 1L))
                .when(notificationService).readNotification(any(), any());

        mvc.perform(post(NotificationUri.READ, 1L)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void count_unread_notifications() throws Exception {
        // given
        when(notificationService.countUncheckedByReceiverId(any()))
                .thenReturn(12);

        // then
        mvc.perform(get(NotificationUri.COUNT_UNREAD)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    private static class NotificationUri {
        public static String BASE = "/notifications";
        public static String RECEIVER_PARAM = BASE + "?receiver={}";
        public static String READ = BASE + "/{notificationId}" + "/read";
        public static String COUNT_UNREAD = BASE + "/counts/unread";
    }
}