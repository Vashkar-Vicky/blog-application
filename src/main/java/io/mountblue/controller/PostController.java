package io.mountblue.controller;

import io.mountblue.dao.UserRepository;
import io.mountblue.model.Comment;
import io.mountblue.model.Post;
import io.mountblue.model.Tags;
import io.mountblue.model.User;
import io.mountblue.service.CommentService;
import io.mountblue.service.PostService;
import io.mountblue.service.TagService;
import io.mountblue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @GetMapping
    public String getAllPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return "posts/list";
    }

    @GetMapping("/create")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create-post";
    }

    @PostMapping("/createNew")
    public String createPost(@ModelAttribute Post post, @RequestParam String tag) {

        postService.createPost(post,tag);
         return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String getPostDetails(@PathVariable UUID id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "posts/post-details";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable UUID id) {
        postService.delete(id);
        return "redirect:/posts";
    }

    @GetMapping("/filter")
    public String filterPosts(@RequestParam(required = false) String author,
                              @RequestParam(required = false) String tag,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Model model) {

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atStartOfDay().plusDays(1).minusNanos(1) : null; // Include full end date

        User user = null;

        if (author != null && !author.isEmpty()) {
            user = userService.findByUsername(author);
            if (user == null) {
                // Handle case where user is not found
                model.addAttribute("error", "No user found with the username: " + author);
                return "posts/list";
            }
        }
        List<Post> filteredPosts = postService.getFilteredPosts(user, tag, startDateTime, endDateTime);
        model.addAttribute("posts", filteredPosts);
        return "posts/list";
    }


    @GetMapping("/posts")
    public String getAllPosts(@RequestParam(defaultValue = "asc") String sort, Model model) {
        if ("desc".equalsIgnoreCase(sort)) {
            model.addAttribute("posts", postService.getAllPostsSortedByPublishedDateDesc());
        } else {
            model.addAttribute("posts", postService.getAllPostsSortedByPublishedDateAsc());
        }
        return "posts/list";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query, Model model) {
        List<Post> posts = postService.searchPosts(query);
        model.addAttribute("posts", posts);
        model.addAttribute("query", query);
        return "posts/list";
    }


    @GetMapping("/update/{id}")
    public String updatePostForm(@PathVariable UUID id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);

        String tagsString = post.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.joining(", "));
        model.addAttribute("tagsInput", tagsString);

        return "posts/post-update";
    }

    @PostMapping("/update")
    public String updatePost(@RequestParam("id") String id,
                             @RequestParam("tagsInput") String tagsInput,
                             @RequestParam("title") String title,
                             @RequestParam("excerpt") String excerpt,
                             @RequestParam("content") String content,Model model) {
        UUID uuid = UUID.fromString(id);
        Post post = postService.getPostById(uuid);

        postService.updatePost(uuid, title, excerpt, content);
        postService.updatePostTags(uuid, tagsInput);

        model.addAttribute("post", post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable UUID id, @ModelAttribute Comment comment) {
        Post post = postService.getPostById(id);
        comment.setPost(post);
        commentService.save(comment);
        return "redirect:/posts/" + id;
    }
}
