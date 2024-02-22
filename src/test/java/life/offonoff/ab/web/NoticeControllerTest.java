package life.offonoff.ab.web;

import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.notice.NotificationType;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import life.offonoff.ab.web.response.notice.message.NoticeMessageTemplate;
import life.offonoff.ab.web.response.notice.message.VoteCountOnTopicNoticeMessage;
import life.offonoff.ab.web.response.notice.message.VoteResultNoticeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;

import static life.offonoff.ab.domain.notice.NotificationType.VOTE_COUNT_ON_TOPIC_NOTIFICATION;
import static life.offonoff.ab.web.response.notice.message.NoticeMessageTemplate.VOTE_COUNT_ON_TOPIC_TITLE;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = NoticeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
class NoticeControllerTest extends RestDocsTest {

    @MockBean
    NoticeService noticeService;

    @Test
    void get_members_notifications() throws Exception {
        // given
        NoticeResponse voteResultResponse = new NoticeResponse(
                VOTE_COUNT_ON_TOPIC_NOTIFICATION,
                false,
                new VoteResultNoticeMessage("test_title1", 1L));

        NoticeResponse voteCountResponse = new NoticeResponse(
                VOTE_COUNT_ON_TOPIC_NOTIFICATION,
                false,
                new VoteCountOnTopicNoticeMessage(VOTE_COUNT_ON_TOPIC_TITLE.formatted(100), "test_title2", 2L));

        when(noticeService.findAllByReceiverId(nullable(Long.class)))
                .thenReturn(List.of(voteResultResponse, voteCountResponse));

        // then
        mvc.perform(get(NoticeUri.BASE)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    private static class NoticeUri {
        public static String BASE = "/notices";
    }
}