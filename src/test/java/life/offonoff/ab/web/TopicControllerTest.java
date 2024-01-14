package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.application.service.request.*;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.CommentResponse;
import life.offonoff.ab.web.response.topic.TopicResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
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

    @MockBean
    CommentService commentService;

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
    void getTopicSlice_default() throws Exception {

        Slice<TopicResponse> topicResponseSlice = createDefaultTopicSlice();

        when(topicService.findAll(nullable(Long.class), any(TopicSearchRequest.class), any(Pageable.class)))
                .thenReturn(topicResponseSlice);

        mvc.perform(
                        get(TopicUri.RETRIEVE_IN_VOTING)
                                .header("AUTHORIZATION", "Bearer ACCESS_TOKEN"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(queryParameters(
                        parameterWithName("keyword_id").description("토픽 키워드 ID").optional(),
                        parameterWithName("page").description("page number - default `0`").optional(),
                        parameterWithName("size").description("page size - default `10` [min, max] [0, 100]").optional(),
                        parameterWithName("sort").description("orderBy - default `voteCount,desc`").optional())));
    }

    @Test
    @WithMockUser
    void getTopicSlice_filtered_by_keyword() throws Exception {
        Slice<TopicResponse> topicSlice = createTopicSliceFilteredByKeyword(1L);

        when(topicService.findAll(nullable(Long.class), any(TopicSearchRequest.class), any(Pageable.class)))
                .thenReturn(topicSlice);

        mvc.perform(
                        get(TopicUri.RETRIEVE_IN_VOTING)
                                .header("AUTHORIZATION", "Bearer ACCESS_TOKEN")
                                .param("keyword_id", String.valueOf(1L)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTopicSlice_default_for_unauthorized_member() throws Exception {

        Slice<TopicResponse> topicResponseSlice = createDefaultTopicSlice();

        when(topicService.findAll(isNull(Long.class), any(TopicSearchRequest.class), any(Pageable.class)))
                .thenReturn(topicResponseSlice);

        mvc.perform(get(TopicUri.RETRIEVE_IN_VOTING))
                .andExpect(status().isOk());
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
        mvc.perform(patch(TopicUri.STATUS, 1, false).with(csrf().asHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateTopic_byNonAuthorUser_IllegalTopicStatusChangeException() throws Exception {
        doThrow(new IllegalTopicStatusChangeException(1L, 2L))
                .when(topicService).activateMembersTopic(any(), any(), any());

        mvc.perform(patch(TopicUri.STATUS, 2, false).with(csrf().asHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("ILLEGAL_TOPIC_STATUS_CHANGE"));
    }

    @Test
    void deleteTopic() throws Exception {
        mvc.perform(delete(TopicUri.DELETE, 1).with(csrf().asHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void voteForTopic_byNonAuthor_success() throws Exception {
        when(commentService.getLatestCommentOfTopic(any()))
                .thenReturn(CommentResponse.from(
                        new Comment(
                        createRandomMember(),
                        createRandomTopic(),
                        ChoiceOption.CHOICE_A,
                "content"
                )));

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());

        mvc.perform(post(TopicUri.VOTE, 1).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestComment.content").value("content"));
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
    
    @Test
    void modifyVoteForTopic_not_duplicated_option() throws Exception {

        VoteModifyRequest request = new VoteModifyRequest(
                ChoiceOption.CHOICE_B, getEpochSecond(LocalDateTime.now().plusMinutes(30))
        );

        mvc.perform(patch(TopicUri.VOTE, 1).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                .writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    void modifyVoteForTopic_exception_duplicated_option() throws Exception {

        Long topicId = 1L;
        ChoiceOption modifiedOption = ChoiceOption.CHOICE_A;

        VoteModifyRequest request = new VoteModifyRequest(
                modifiedOption, getEpochSecond(LocalDateTime.now().plusMinutes(30))
        );

        doThrow(new DuplicateVoteException(topicId, modifiedOption))
                .when(topicService).modifyVoteForTopicByMember(any(), any(), any());

        mvc.perform(patch(TopicUri.VOTE, topicId).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                .writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.abCode").value(AbCode.DUPLICATE_VOTE.name())
                );
    }
    
    @Test
    void getTopCommentOfTopic() throws Exception {
        when(commentService.getLatestCommentOfTopic(any()))
                .thenReturn(CommentResponse.from(
                        new Comment(
                                createRandomMember(),
                                createRandomTopic(),
                                ChoiceOption.CHOICE_A,
                                "content"
                        )));

        mvc.perform(get(TopicUri.TOPIC_COMMENT, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestComment.content").value("content"));
    }

    private Slice<TopicResponse> createDefaultTopicSlice() {
        // create author
        Member author = TestMember.builder()
                .id(1L)
                .nickname("nicknameA")
                .build().buildMember();

        // create retrieve member
        Member retrieveMember = TestMember.builder()
                .id(1L)
                .nickname("retrieveMember")
                .build().buildMember();

        // create keyword
        Keyword keyword1 = TestKeyword.builder()
                .id(1L)
                .name("key1")
                .build().buildKeyword();

        Keyword keyword2 = TestKeyword.builder()
                .id(2L)
                .name("key2")
                .build().buildKeyword();

        // create Topic
        Topic topic1 = TestTopic.builder()
                .id(1L)
                .title("title" + 1L)
                .side(TopicSide.TOPIC_A)
                .author(author)
                .keyword(keyword1)
                .voteCount(1000)
                .build().buildTopic();

        Topic topic2 = TestTopic.builder()
                .id(2L)
                .title("title" + 2L)
                .side(TopicSide.TOPIC_A)
                .author(author)
                .keyword(keyword2)
                .voteCount(2000)
                .build().buildTopic();

        PageRequest pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "voteCount");

        return PagingUtil.toSlice(
                Stream.of(topic1, topic2)
                        .sorted((t1, t2) -> t2.getVoteCount() - t1.getVoteCount())
                        .map(t -> TopicResponse.from(t, retrieveMember))
                        .collect(Collectors.toList()),
                pageable);
    }

    private Slice<TopicResponse> createTopicSliceFilteredByKeyword(Long keywordId) {
        // create author
        Member author = TestMember.builder()
                .id(1L)
                .nickname("nicknameA")
                .build().buildMember();

        // create retrieve member
        Member retrieveMember = TestMember.builder()
                .id(1L)
                .nickname("retrieveMember")
                .build().buildMember();

        // create keyword
        Keyword keyword = TestKeyword.builder()
                .id(keywordId)
                .name("key")
                .build().buildKeyword();

        // create Topic
        Topic topic1 = TestTopic.builder()
                .id(1L)
                .title("title" + 1L)
                .side(TopicSide.TOPIC_A)
                .author(author)
                .keyword(keyword)
                .voteCount(1000)
                .build().buildTopic();

        Topic topic2 = TestTopic.builder()
                .id(2L)
                .title("title" + 2L)
                .side(TopicSide.TOPIC_A)
                .author(author)
                .keyword(keyword)
                .voteCount(2000)
                .build().buildTopic();

        PageRequest pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "voteCount");

        return PagingUtil.toSlice(
                Stream.of(topic1, topic2)
                        .sorted((t1, t2) -> t2.getVoteCount() - t1.getVoteCount())
                        .map(t -> TopicResponse.from(t, retrieveMember))
                        .collect(Collectors.toList()),
                pageable);
    }


    private static class TopicUri {
        private static final String BASE = "/topics";
        private static final String INFO = "/info";
        private static final String VOTING = "/voting";
        private static final String RETRIEVE_IN_VOTING = BASE + INFO + VOTING;
        private static final String REPORT = BASE + "/{topicId}/report";
        private static final String STATUS = BASE + "/{topicId}/status?active={active}";
        private static final String VOTE = BASE + "/{topicId}/vote";
        private static final String DELETE = BASE + "/{topicId}";
        private static final String TOPIC_COMMENT = BASE + "/{topicId}/comment";
    }
}
