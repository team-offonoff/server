package life.offonoff.ab.web;

import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.config.WebConfig;

import life.offonoff.ab.repository.pagination.PagingUtil;
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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
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
        Long topicId = 1L;
        Long commentId = 1L; String content = "content";
        int hates = 0; int likes = 0;
        Long writerId = 1L; String writerNickname = "writer"; String writerProfileImageUrl = "imageUrl";

        CommentRequest request = new CommentRequest(topicId, content);
        CommentResponse response = new CommentResponse(
                commentId,
                topicId,
                new MemberResponse(writerId, writerNickname, writerProfileImageUrl),
                content,
                likes,
                hates
        );

        when(commentService.register(nullable(Long.class), any(CommentRequest.class))).thenReturn(response);

        mvc.perform(post(CommentUri.BASE)
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.writerId").value(writerId))
                .andDo(print());
    }

    @Test
    void get_comments_of_topic() throws Exception {
        // give
        Long topicId = 1L;

        // comment1
        CommentResponse response1 = new CommentResponse(1L, topicId, new MemberResponse(1L, "member1", "imageUrl1"), "content1", 0, 0);
        CommentResponse response2 = new CommentResponse(2L, topicId,  new MemberResponse(2L, "member2", "imageUrl2"), "content2", 0, 0);

        when(commentService.findAll(anyLong(), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(response1, response2)));

        mvc.perform(get(CommentUri.BASE)
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

    private static class CommentUri {
        private static final String BASE = "/comments";
    }
}
