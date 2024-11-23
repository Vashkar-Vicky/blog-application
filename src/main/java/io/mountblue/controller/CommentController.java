package io.mountblue.controller;

import io.mountblue.model.Comment;
import io.mountblue.model.Post;
import io.mountblue.service.CommentService;
import io.mountblue.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

//    @GetMapping
//    public String viewComment(@PathVariable UUID id, Model model){
//        Comment comment = commentService.getById(id);
//        model.addAttribute("comment", comment);
//        return "posts/post-details";
//    }

    @PostMapping
    public String addComment(@RequestParam String message, @RequestParam String postId) {
        UUID uuid = UUID.fromString(postId);
        commentService.addComment(message , uuid);

        return "redirect:/posts/" + postId;
    }
    @PutMapping("/{id}")
    public String updateComment(@PathVariable("id") UUID commentId, @RequestParam String message,
                                @RequestParam String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();



        commentService.updateComment(commentId,message);
        return "redirect:/posts/" + postId;
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable("id") UUID commentId, @RequestParam UUID postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }
}
