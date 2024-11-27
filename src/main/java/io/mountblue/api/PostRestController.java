package io.mountblue.api;

import io.mountblue.exception.ResourceNotFoundException;
import io.mountblue.model.Comment;
import io.mountblue.model.Post;
import io.mountblue.service.CommentService;
import io.mountblue.service.PostService;
import io.mountblue.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getAllPosts(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("totalPages", posts.getTotalPages());
        response.put("currentPage", page);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody Post post, @RequestParam String tag) {
        postService.createPost(post, tag);
        return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostDetails(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post with ID " + id + " not found.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = userService.isAdmin();
        List<Comment> comments = commentService.getAllComment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("currentUser", currentUsername);
        response.put("comments", comments);
        response.put("isAdmin", isAdmin);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterPosts(@RequestParam(required = false) String author,
                                                           @RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate,
                                                           @RequestParam(required = false) String tag,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.filterPosts(author, startDate, endDate, tag, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postPage.getContent());
        response.put("totalPages", postPage.getTotalPages());
        response.put("currentPage", page);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable UUID id,
                                           @RequestBody Map<String, String> postData) {
        Post post = postService.getPostById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post with ID " + id + " not found.");
        }

        String title = postData.get("title");
        String excerpt = postData.get("excerpt");
        String content = postData.get("content");
        String tagsInput = postData.get("tagsInput");

        postService.updatePost(id, title, excerpt, content);
        postService.updatePostTags(id, tagsInput);

        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPosts(@RequestParam("query") String query,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.searchPosts(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("totalPages", posts.getTotalPages());
        response.put("currentPage", page);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sort")
    public ResponseEntity<Map<String, Object>> sortPosts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "publishedAt") String sortBy,
                                                         @RequestParam(defaultValue = "true") boolean ascending) {
        Page<Post> sortedPosts = postService.sortPosts(page, size, sortBy, ascending);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", sortedPosts.getContent());
        response.put("currentPage", sortedPosts.getNumber());
        response.put("totalPages", sortedPosts.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
