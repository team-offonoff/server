package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.service.TopicService;
import life.offonoff.ab.service.TopicServiceTest.TopicTestDtoHelper;
import life.offonoff.ab.service.TopicServiceTest.TopicTestDtoHelper.TopicTestDtoHelperBuilder;
import life.offonoff.ab.service.request.TopicCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

        mvc.perform(post(TopicUri.BASE).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule()) // For serializing localdatetime
                                             .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private static class TopicUri {
        private static String BASE = "/topics";
    }
}
