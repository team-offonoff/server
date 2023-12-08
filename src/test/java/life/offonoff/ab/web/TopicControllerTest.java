package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.VoteCancelRequest;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.topic.TopicResponse;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        when(topicService.findAll(any(), any())).thenReturn(createTopicSlice());

        mvc.perform(
                        get(TopicUri.BASE + TopicUri.OPENED + TopicUri.NOW)
                                .param("hidden", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private Slice<TopicResponse> createTopicSlice() {
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
        return PagingUtil.toSlice(topics, pageable)
                .map(TopicResponse::from);
    }

    @Test
    void createTopicReport() throws Exception {
        mvc.perform(post(TopicUri.REPORT, 1).with(csrf().asHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void createTopicReport_withNonExistentTopic_TopicNotFoundException() throws Exception {
        doThrow(new TopicNotFoundException(1L))
                .when(topicService).reportTopicByMember(any(), any());

        mvc.perform(post(TopicUri.REPORT, 1).with(csrf().asHeader())
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

        mvc.perform(post(TopicUri.REPORT, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("DUPLICATE_TOPIC_REPORT"))
                .andDo(print());
    }

    @Test
    void deactivateTopic() throws Exception {
        mvc.perform(patch(TopicUri.REMOVE, 1, false).with(csrf().asHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateTopic_byNonAuthorUser_IllegalTopicStatusChangeException() throws Exception {
        doThrow(new IllegalTopicStatusChangeException(1L, 2L))
                .when(topicService).activateMembersTopic(any(), any(), any());

        mvc.perform(patch(TopicUri.REMOVE, 2, false).with(csrf().asHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("ILLEGAL_TOPIC_STATUS_CHANGE"));
    }

    @Test
    void voteForTopic_byNonAuthor_success() throws Exception {
        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());

        mvc.perform(post(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void voteForTopic_byAuthor_throwException() throws Exception {
        doThrow(new VoteByAuthorException(1L, 2L))
                .when(topicService).voteForTopicByMember(any(), any(), any());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        mvc.perform(post(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("VOTED_BY_AUTHOR"));
    }

    @Test
    void voteForTopic_votedAtFuture_throwException() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime votedAt = now.plusMinutes(30);

        doThrow(new FutureTimeRequestException(votedAt, now))
                .when(topicService).voteForTopicByMember(any(), any(), any());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, votedAt.atZone(ZoneId.systemDefault()).toEpochSecond());
        mvc.perform(post(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("FUTURE_TIME_REQUEST"));
    }

    @Test
    void cancelVoteForTopic_existingVote_success() throws Exception {
        VoteCancelRequest request = new VoteCancelRequest(
                LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toEpochSecond()
        );
        mvc.perform(delete(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelVoteForTopic_nonExistingVote_throwException() throws Exception {
        doThrow(new MemberNotVoteException(2L, 1L))
                .when(topicService).cancelVoteForTopicByMember(any(), any(), any());
        VoteCancelRequest request = new VoteCancelRequest(
                LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toEpochSecond()
        );
        mvc.perform(delete(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("MEMBER_NOT_VOTE"));
    }

    private static class TopicUri {
        private static final String BASE = "/topics";
        private static final String OPENED = "/open";
        private static final String NOW = "/now";
        private static final String REPORT = BASE + "/{topicId}/report";
        private static final String REMOVE = BASE + "/{topicId}/status?active={active}";
        private static final String VOTE = BASE + "/{topicId}/vote";
    }
}
