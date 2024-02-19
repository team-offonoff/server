package life.offonoff.ab.web;

import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import life.offonoff.ab.web.response.notice.VoteCountOnTopicNoticeResponse;
import life.offonoff.ab.web.response.notice.VoteResultNoticeResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
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
        NoticeResponse response1 = new VoteResultNoticeResponse(false, 1L, "topic1_title");
        NoticeResponse response2 = new VoteCountOnTopicNoticeResponse(true, 2L, "topic2_title", 100);

        when(noticeService.findAllByReceiverId(nullable(Long.class)))
                .thenReturn(List.of(response1, response2));

        // then
        mvc.perform(get(NoticeUri.BASE)
                        .header("Authorization", "Bearer Access_Token"))
                .andExpect(status().isOk());
    }

    private static class NoticeUri {
        public static String BASE = "/notices";
    }
}