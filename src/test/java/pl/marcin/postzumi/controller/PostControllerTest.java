package pl.marcin.postzumi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.marcin.postzumi.model.Post;
import pl.marcin.postzumi.repository.PostRepository;
import pl.marcin.postzumi.service.PostService;

import java.util.List;
import java.util.Map;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        postService.updatePosts();
    }

    @Test
    public void shouldReturnAllPost() throws Exception {
        ResultActions actions = mvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(print());

        String result = actions.andReturn().getResponse().getContentAsString();
        Post[] posts = mapper.readValue(result, Post[].class);
        Assertions.assertEquals(posts.length, 100);

        verify(postRepository, times(100)).save(any(Post.class));
    }

    @Test
    public void shouldReturnPostWithTitle() throws Exception {
        String title = "qui est esse";
        String body = "est rerum tempore vitae\n" +
                "sequi sint nihil reprehenderit dolor beatae ea dolores neque\n" +
                "fugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\n" +
                "qui aperiam non debitis possimus qui neque nisi nulla";
        List<Post> posts = List.of(new Post(2L, title, body, 1L));

        when(postService.getPostsByTitle(title)).thenReturn(posts);

        ResultActions actions = mvc.perform(get("/posts/qui est esse")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
        String result = actions.andReturn().getResponse().getContentAsString();
        Post[] bodyPosts = mapper.readValue(result, Post[].class);
        Assertions.assertEquals(bodyPosts[0].getTitle(), title);
    }

    @Test
    public void shouldReturnPostWithTitleNoUserId() throws Exception {
        String title = "qui est esse";
        String body = "est rerum tempore vitae\n" +
                "sequi sint nihil reprehenderit dolor beatae ea dolores neque\n" +
                "fugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\n" +
                "qui aperiam non debitis possimus qui neque nisi nulla";
        List<Post> posts = List.of(new Post(2L, title, body, 1L));

        when(postService.getPostsByTitle(title)).thenReturn(posts);

        ResultActions actions = mvc.perform(get("/posts/qui est esse/noUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
        String result = actions.andReturn().getResponse().getContentAsString();
        Post[] bodyPosts = mapper.readValue(result, Post[].class);
        Assertions.assertEquals(bodyPosts[0].getTitle(), title);
        Assertions.assertNull(bodyPosts[0].getUserId());
    }

    @Test
    public void shouldUpdateAndReturnPost() throws Exception {
        String newTitle = "Lorem ipsum dolor sit amet";
        String newBody = "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.";

        ResultActions actions = mvc.perform(put("/posts/2")
                .param("title", newTitle)
                .param("body", newBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
        String result = actions.andReturn().getResponse().getContentAsString();
        Post bodyPost = mapper.readValue(result, Post.class);
        Assertions.assertEquals(bodyPost.getTitle(), newTitle);
        Assertions.assertEquals(bodyPost.getBody(), newBody);
    }

    @Test
    public void shouldReturnNoContentWhileUpdate() throws Exception {
        String newTitle = "Lorem ipsum dolor sit amet";
        String newBody = "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.";

        mvc.perform(put("/posts/2")
                .param("title", newTitle)
                .param("body", newBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(postRepository, never()).findById(any(Long.class));
    }

    @Test
    public void shouldReturnSuccessStatusWhileDelete() throws Exception {
        mvc.perform(delete("/posts/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        verify(postRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    public void shouldReturnBadRequestStatusWhileDelete() throws Exception {
        mvc.perform(delete("/posts/200")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(postRepository, never()).deleteById(any(Long.class));
    }
}
