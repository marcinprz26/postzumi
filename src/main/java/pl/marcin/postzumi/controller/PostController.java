package pl.marcin.postzumi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.marcin.postzumi.model.Post;
import pl.marcin.postzumi.model.PostNoUser;
import pl.marcin.postzumi.service.PostService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("posts")
@Validated
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping(value = {"", "/{title}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Post>> getAllPosts(@PathVariable(required = false) String title) {
        List<Post> posts;
        if(title == null || title.isBlank()) {
            posts = postService.getAllPosts();
        } else {
            posts = postService.getPostsByTitle(title);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping(value = {"/noUser", "/{title}/noUser"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostNoUser>> getAllPostsNoUser(@PathVariable(required = false) String title) {
        List<Post> posts;
        ModelMapper mapper = new ModelMapper();
        if(title == null || title.isBlank()) {
            posts = postService.getAllPosts();
        } else {
            posts = postService.getPostsByTitle(title);
        }
        List<PostNoUser> postsNoUser = posts.stream()
                .map(post -> mapper.map(post, PostNoUser.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(postsNoUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @NotBlank @Size(max = 150) @RequestParam String title,
                                           @Size(max = 350) @RequestParam String body) {
        Optional<Post> updatedOptional = postService.updatePost(id, title, body);
        return updatedOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable("id") Long id) {
        return postService.deletePost(id) ?
            ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

}
