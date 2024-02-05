package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.KeywordService;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.application.service.request.*;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.domain.TestEntityUtil;
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
import life.offonoff.ab.web.response.KeywordResponse;
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
import java.util.List;
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

@WebMvcTest(value = KeywordController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtProvider.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
public class KeywordControllerTest extends RestDocsTest {

    @MockBean
    KeywordService keywordService;

    @Test
    @WithMockUser
    void getKeywordSlice_by_side() throws Exception {

        Slice<KeywordResponse> keywordResponseSlice = createDefaultKeywordSlice();

        when(keywordService.findAllByTopicSide(any(), any()))
                .thenReturn(keywordResponseSlice);

        mvc.perform(get(KeywordUri.BASE)
                        .queryParam("side", TopicSide.TOPIC_B.name()))
                .andExpect(status().isOk())
                .andDo(restDocs.document(queryParameters(
                        parameterWithName("side").description("토픽 키워드 SIDE - required"),
                        parameterWithName("page").description("page number - default `0`").optional(),
                        parameterWithName("size").description("page size - default `20` [min, max] [0, 100]").optional(),
                        parameterWithName("sort").description("orderBy - default `id,asc`").optional())));
    }

    private Slice<KeywordResponse> createDefaultKeywordSlice() {
        Keyword keyword1 = TestKeyword.builder()
                .id(1L)
                .side(TopicSide.TOPIC_B)
                .name("key1")
                .build().buildKeyword();

        Keyword keyword2 = TestKeyword.builder()
                .id(2L)
                .side(TopicSide.TOPIC_B)
                .name("key2")
                .build().buildKeyword();

        List<KeywordResponse> keywordResponses = List.of(keyword1, keyword2)
                .stream()
                .map(KeywordResponse::from)
                .toList();

        Pageable pageable = createPageableAsc(0, 2, "id");

        return PagingUtil.toSlice(keywordResponses, pageable);
    }

    private static class KeywordUri {
        private static final String BASE = "/keywords";
    }
}
