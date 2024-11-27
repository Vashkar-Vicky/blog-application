package io.mountblue.api;

import io.mountblue.exception.CommentNotFoundException;
import io.mountblue.model.Comment;
import io.mountblue.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<String> addComment(@RequestParam String message, @RequestParam String postId) {
        UUID uuid = UUID.fromString(postId);
        commentService.addComment(message, uuid);

        return ResponseEntity.ok("Comment added successfully for Post ID: " + postId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable("id") UUID commentId, @RequestParam String message) {
        if (!commentService.existsById(commentId)) {
            throw new CommentNotFoundException("Comment with ID " + commentId + " not found");
        }
        commentService.updateComment(commentId, message);

        return ResponseEntity.ok("Comment updated successfully with ID: " + commentId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") UUID commentId) {
        if (!commentService.existsById(commentId)) {
            throw new CommentNotFoundException("Comment with ID " + commentId + " not found");
        }
        commentService.deleteComment(commentId);

        return ResponseEntity.ok("Comment deleted successfully with ID: " + commentId);
    }
}
