package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.exception.TopicNotFoundException;
import life.offonoff.ab.exception.TopicReportDuplicateException;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TopicController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
public class TopicControllerTest extends RestDocsTest {

    @MockBean
    TopicService topicService;

    @Test
    @WithMockUser
    void createTopic() throws Exception {
        TopicTestDtoHelperBuilder builder = TopicTestDtoHelper.builder();
        TopicCreateRequest request = builder.build().createRequest();

        when(topicService.createMembersTopic(any(), any()))
                .thenReturn(builder.build().createResponse());

        mvc.perform(post(TopicUri.BASE).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        relaxedRequestFields(
                                fieldWithPath("choices[].choiceContentRequest.type").description("현재는 항상 IMAGE_TEXT; 이미지와 텍스트가 아닌 다른 선택지 종류가 추가될 수 있어서 만들어 놓은 필드기 때문임."),
                                fieldWithPath("deadline").type(Long.TYPE).description("Unix timestamps in seconds")
                        )));
    }

    @Test
    @WithMockUser
    void getTopicSlice() throws Exception {
        when(topicService.searchAll(any(), any())).thenReturn(createTopicSlice());

        mvc.perform(
                        get(TopicUri.BASE + TopicUri.OPENED + TopicUri.NOW)
                                .param("hidden", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void createTopicReport() throws Exception {
        mvc.perform(post(TopicUri.BASE + "/1/report").with(csrf().asHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void createTopicReport_withNonExistentTopic_TopicNotFoundException() throws Exception {
        doThrow(new TopicNotFoundException(1L))
                .when(topicService).reportTopicByMember(any(), any());

        mvc.perform(post(TopicUri.BASE + "/1/report").with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("abCode").value("TOPIC_NOT_FOUND"))
                .andDo(print());
    }

    @Test
    void createTopicReport_alreadyReported_TopicReportDuplicateException() throws Exception {
        doThrow(new TopicReportDuplicateException(1L, 2L))
                .when(topicService).reportTopicByMember(any(), any());

        mvc.perform(post(TopicUri.BASE + "/1/report").with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("DUPLICATE_TOPIC_REPORT"))
                .andDo(print());
    }

    private Slice<Topic> createTopicSlice() {
        PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "voteCount");
        Comparator<Topic> voteCountDesc = (t1, t2) -> t2.getVoteCount() - t1.getVoteCount();

        // create Member
        Member author = TestMember.builder()
                .id(1L)
                .nickname("nicknameA")
                .build().buildMember();

        // create Keyword
        Keyword keyword = TestKeyword.builder()
                .id(1L)
                .name("keywordA")
                .build().buildKeyword();

        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Topic topic = TestTopic
                    .builder()
                    .id((long) (i + 1))
                    .title("title" + i)
                    .author(author)
                    .keyword(keyword)
                    .voteCount(i)
                    .build()
                    .buildTopic();

            topics.add(topic);
        }
        topics.sort(voteCountDesc);
        return PagingUtil.toSlice(topics, pageable);
    }

    private static class TopicUri {
        private static String BASE = "/topics";
        private static String OPENED = "/open";
        private static String NOW = "/now";
    }
}
