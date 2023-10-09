package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.service.TopicService;
import life.offonoff.ab.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.service.request.TopicCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
public class TopicControllerTest extends RestDocsTest {

    @MockBean
    TopicService topicService;

    @Test
    @WithMockUser
    void createCategory() throws Exception {
        TopicTestDtoHelperBuilder builder = TopicTestDtoHelper.builder();
        TopicCreateRequest request = builder.build().createRequest();
        when(topicService.createMembersTopic(any(), any()))
                .thenReturn(builder.build().createResponse());

        mvc.perform(post(TopicUri.BASE).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
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

    private Slice<Topic> createTopicSlice() {
        PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "voteCount");
        Comparator<Topic> voteCountDesc = (t1, t2) -> t2.getVoteCount() - t1.getVoteCount();

        Member publishMember = TestEntityUtil.createMember(1);
        Category category = TestEntityUtil.createCategory(1);
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Topic topic = TestEntityUtil.TestTopic
                    .builder()
                    .title("title" + i)
                    .publishMember(publishMember)
                    .category(category)
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
