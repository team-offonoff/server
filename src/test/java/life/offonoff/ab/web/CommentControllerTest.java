package life.offonoff.ab.web;

import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.application.service.request.CommentUpdateRequest;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import life.offonoff.ab.web.response.CommentResponse;
import life.offonoff.ab.web.response.MemberResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import java.util.Collections;
import java.util.List;

import static life.offonoff.ab.application.service.common.LengthInfo.COMMENT_CONTENT;
import static life.offonoff.ab.exception.AbCode.INVALID_LENGTH_OF_FIELD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
class CommentControllerTest extends RestDocsTest {

    @MockBean
    CommentService commentService;

    @Test
    void createComment() throws Exception {
        Long writerId = 1L;
        Long topicId = 1L;
        String content = "content";

        CommentRequest request = new CommentRequest(topicId, content);
        CommentResponse response = new CommentResponse(
                1L,
                topicId,
                new MemberResponse(writerId, "writerNickname", "writerProfileImageUrl"),
                content,
                0,
                0,
                false,
                false
        );

        when(commentService.register(nullable(Long.class), any(CommentRequest.class))).thenReturn(response);

        mvc.perform(post(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.writer.id").value(writerId))
                .andDo(print());
    }

    @Test
    void createComment_invalid_topic_exception() throws Exception {
        Long topicId = 1L;
        String content = "content";

        CommentRequest request = new CommentRequest(topicId, content);

        when(commentService.register(nullable(Long.class), any(CommentRequest.class)))
                .thenThrow(new TopicNotFoundException(topicId));

        mvc.perform(post(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.abCode").value(AbCode.TOPIC_NOT_FOUND.name()));
    }

    @Test
    void createComment_withLongContent_error() throws Exception {
        doThrow(new LengthInvalidException("댓글 내용", COMMENT_CONTENT.getMinLength(), COMMENT_CONTENT.getMaxLength()))
                .when(commentService).register(any(), any());

        CommentRequest request =
                new CommentRequest(2L, "c".repeat(COMMENT_CONTENT.getMaxLength() + 1));
        mvc.perform(post(CommentUri.BASE)
                            .header("Authorization", "Bearer ACCESS_TOKEN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value(INVALID_LENGTH_OF_FIELD.name()))
                .andDo(print());
    }

    @Test
    void get_comments_of_topic() throws Exception {
        // give
        Long topicId = 1L;

        when(commentService.findAll(nullable(Long.class), anyLong(), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(createCommentResponses(topicId)));

        mvc.perform(get(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .queryParam("topic-id", String.valueOf(topicId))
                        .queryParam("page", String.valueOf(0))
                        .queryParam("size", String.valueOf(50)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        relaxedQueryParameters(
                                parameterWithName("topic-id").description("comments of Topic"),
                                parameterWithName("page").description("page number; default 0"),
                                parameterWithName("size").description("page size; default 50")
                        )
                ));
    }

    @Test
    void get_comments_of_topic_empty_comments() throws Exception {
        Long topicId = 1L;

        when(commentService.findAll(isNull(Long.class), anyLong(), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(Collections.emptyList()));

        mvc.perform(get(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .queryParam("topic-id", String.valueOf(topicId))
                        .queryParam("page", String.valueOf(0))
                        .queryParam("size", String.valueOf(50)))
                .andExpect(status().isOk());
    }

    @Test
    void get_comments_exception_topic_not_found() throws Exception {
        Long topicId = 1L;

        when(commentService.findAll(nullable(Long.class), anyLong(), any(Pageable.class)))
                .thenThrow(new TopicNotFoundException(topicId));

        mvc.perform(get(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .queryParam("topic-id", String.valueOf(topicId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_comments_exception_not_voted() throws Exception {
        Long topicId = 1L;

        when(commentService.findAll(nullable(Long.class), anyLong(), any(Pageable.class)))
                .thenThrow(new UnableToViewCommentsException(topicId));

        mvc.perform(get(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .queryParam("topic-id", String.valueOf(topicId)))
                .andExpect(status().isBadRequest());
    }

    private List<CommentResponse> createCommentResponses(Long topicId) {
        CommentResponse response1 = new CommentResponse(
                1L,
                topicId,
                new MemberResponse(1L, "member1", "imageUrl1"),
                "content1",
                0,
                0,
                true,
                false);
        CommentResponse response2 = new CommentResponse(
                2L,
                topicId,
                new MemberResponse(2L, "member2", "imageUrl2"),
                "content2",
                0,
                0,
                true,
                false);

        return List.of(response1, response2);
    }

    @Test
    void delete_comment() throws Exception {
        Long commentId = 1L;

        mvc.perform(delete(CommentUri.DELETE, commentId)
                        .header("Authorization", "Bearer ACCESS_TOKEN"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteComment_withInvalidCommentId_error() throws Exception {
        doThrow(new CommentNotFoundException(1L))
                .when(commentService).deleteComment(any(), any());

        mvc.perform(delete(CommentUri.DELETE, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_comment_member_cannot_touch() throws Exception {
        Long commentId = 1L;
        Long accessMemberId = 1L;

        doThrow(new IllegalCommentStatusChangeException(accessMemberId, commentId))
                .when(commentService).deleteComment(nullable(Long.class), anyLong());

        mvc.perform(delete(CommentUri.DELETE, commentId)
                        .header("Authorization", "Bearer ACCESS_TOKEN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void like_comment() throws Exception {
        mvc.perform(post(CommentUri.LIKE, 1L)
                .queryParam("enable", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("enable").description("활성화 여부; `true`시에만 좋아요 처리")
                        )
                ));
    }

    @Test
    void hatee_comment() throws Exception {
        mvc.perform(post(CommentUri.HATE, 1L)
                        .queryParam("enable", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("enable").description("활성화 여부; `true`시에만 싫어요 처리")
                        )
                ));
    }

    @Test
    void modifyComment() throws Exception {
        CommentResponse response = new CommentResponse(
                1L,
                2L,
                new MemberResponse(1L, "writerNickname", "writerProfileImageUrl"),
                "new content",
                0,
                0,
                false,
                false
        );
        when(commentService.modifyMembersCommentContent(any(), any(), any())).thenReturn(response);

        CommentUpdateRequest request = new CommentUpdateRequest("new content");

        mvc.perform(patch(CommentUri.COMMENT, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("new content"));
    }

    private static class CommentUri {
        private static final String BASE = "/comments";
        private static final String COMMENT = BASE + "/{commentId}";
        private static final String DELETE = COMMENT;
        private static final String LIKE = COMMENT + "/like";
        private static final String HATE = COMMENT + "/hate";
    }
}
