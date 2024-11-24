package io.mountblue.service;

import io.mountblue.dao.PostRepository;
import io.mountblue.dao.TagRepository;
import io.mountblue.model.Post;
import io.mountblue.model.Tags;
import io.mountblue.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(UUID id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getFilteredPosts(User user, String tags, LocalDateTime startDate, LocalDateTime endDate) {
        String author = (user != null) ? user.getName() : null;
        System.out.println(tags);

        if (author != null && (tags == null || tags.isEmpty()) && startDate == null && endDate == null) {
            return postRepository.findFilteredPostsByUser(author);
        }
        if (author == null && (tags == null || tags.isEmpty())) {
            return postRepository.filterByDate(startDate, endDate);
        }

        if (tags != null && !tags.isEmpty() && author == null && startDate == null && endDate == null) {
            return postRepository.findFilteredPostsByTags(tags);
        }

        if (author != null && tags != null && !tags.isEmpty()) {
            return postRepository.findByUserAndTagsAndDate(author, tags, startDate, endDate);
        }
        if (author != null) {
            return postRepository.findFilteredPostsByUserAndDate(author, startDate, endDate);
        }
        return postRepository.filterByDate(startDate, endDate);
    }

    public List<Post> getAllPostsSortedByPublishedDateDesc() {
        return postRepository.findAllByOrderByPublishedAtDesc();
    }

    public Object getAllPostsSortedByPublishedDateAsc() {
        return postRepository.findAllByOrderByPublishedAtAsc();
    }

    public List<Post> searchPosts(String query) {
        return postRepository.searchPosts(query);
    }


    public void delete(UUID id) {
        postRepository.deleteById(id);
    }

    public void createPost(Post post, String tagsName) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        if (user != null && user.getRole().equals("USER")) {
            post.setUser(user);
            post.setPublished(true);
        }

        if (tagsName != null && !tagsName.isEmpty()) {
            String[] tagNames = tagsName.split(",");
            Set<Tags> tagsSet = new HashSet<>();

            for (String tagName : tagNames) {
                tagName = tagName.trim();
                Tags tag = tagService.getOrCreateTag(tagName);
                tagsSet.add(tag);
            }
            post.setTags(tagsSet);
        }
        postRepository.save(post);
    }
    public void updatePostTags(UUID postId, String tagsInput) {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByEmail(username);
        if (user != null && user.getRole().equals("USER")) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

            Set<String> tagNames = Stream.of(tagsInput.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toSet());

            Set<Tags> updatedTags = new HashSet<>();
            for (String tagName : tagNames) {
                Tags tag = tagRepository.findByName(tagName);
                updatedTags.add(tag);
            }

            post.setTags(updatedTags);
            postRepository.save(post);
        }
    }

    public void updatePost(UUID uuid, String title, String excerpt, String content){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByEmail(username);
        if (user != null && user.getRole().equals("USER")) {
            Optional<Post> optionalPost = postRepository.findById(uuid);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                post.setTitle(title);
                post.setExcerpt(excerpt);
                post.setContent(content);
                postRepository.save(post);
            }
        }
    }
}