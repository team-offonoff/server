package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.service.CategoryService;
import life.offonoff.ab.service.request.CategoryCreateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser
    void categoryCreateRequest_withNonBlankName_Ok() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("ok");

        MvcResult result = mvc.perform(post(CategoryUri.BASE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser
    void categoryCreateRequest_withBlankName_BadRequest() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("  ");

        MvcResult result = mvc.perform(post(CategoryUri.BASE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    static class CategoryUri {
        public static  String BASE = "/categories";
    }
}