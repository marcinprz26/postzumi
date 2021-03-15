package pl.marcin.postzumi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.marcin.postzumi.model.Post;
import pl.marcin.postzumi.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final String POSTS_API = "https://jsonplaceholder.typicode.com/posts";

    private final WebClient webClient;

    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.webClient = WebClient.builder().build();
        this.postRepository = postRepository;
    }


    public List<Post> getAllPosts() {
        updatePosts();
        return (List<Post>) postRepository.findAll();
    }

    public List<Post> getPostsByTitle(String title) {
        return postRepository.getAllByTitle(title);
    }

    public Optional<Post> updatePost(Long id, String title, String body) {
        Optional<Post> postOptional = postRepository.findById(id);
        if(postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setTitle(title);
            post.setBody(body);
            post.setEdited(true);
            postRepository.save(post);
            return Optional.of(post);
        }
        return Optional.empty();
    }

    public boolean deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Update local database by data retrieved from external API
     */
    @EventListener(ApplicationReadyEvent.class)
    public void updatePosts() {
        List<Post> apiPosts = getPostsFromAPI();
        List<Post> posts = (List<Post>) postRepository.findAll();
        if(posts.isEmpty()) {
            apiPosts.stream(). forEach(post -> postRepository.save(post));
        } else {
            posts.stream()
                    .filter(post -> !post.isEdited())
                    .forEach(post -> {
                        Post apiPost = apiPosts.stream().filter(p -> p.getId().equals(post.getId())).findFirst().orElse(null);
                        if(apiPost != null) {
                            post.setTitle(apiPost.getTitle());
                            post.setBody(apiPost.getBody());
                            post.setEdited(false);
                        }
                    });
        }
    }

    /**
     * Retrieve data from external API
     * @return List of obtained posts
     */
    private List<Post> getPostsFromAPI() {
        Post[] retrievedPosts =
                webClient.get().uri(POSTS_API)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(Post[].class)
                        .block();

        return List.of(retrievedPosts);
    }

    /**
     * Update post data everyday at 2 am
     */
    @Scheduled(cron = "0 0 2 * * *")
    private void updatePostsFromAPI() {
        updatePosts();
    }
}
