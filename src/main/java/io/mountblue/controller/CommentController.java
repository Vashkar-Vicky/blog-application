package io.mountblue.controller;

import io.mountblue.exception.CommentNotFoundException;
import io.mountblue.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public String addComment(@RequestParam String message, @RequestParam String postId) {
        UUID uuid = UUID.fromString(postId);
        commentService.addComment(message, uuid);

        return "redirect:/" + postId;
    }

    @PutMapping("/{id}")
    public String updateComment(@PathVariable("id") UUID commentId, @RequestParam String message,
                                @RequestParam String postId) {
        if (!commentService.existsById(commentId)) {
            throw new CommentNotFoundException("Comment with ID " + commentId + " not found");
        }
        commentService.updateComment(commentId, message);
        return "redirect:/" + postId;
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable("id") UUID commentId, @RequestParam UUID postId) {
        if (!commentService.existsById(commentId)) {
            throw new CommentNotFoundException("Comment with ID " + commentId + " not found");
        }
        commentService.deleteComment(commentId);
        return "redirect:/" + postId;
    }
}
