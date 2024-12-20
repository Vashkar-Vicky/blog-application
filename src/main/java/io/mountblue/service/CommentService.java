package io.mountblue.service;

import io.mountblue.dao.CommentRepository;
import io.mountblue.model.Comment;
import io.mountblue.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    public void addComment(String message, UUID postId) {
        Comment comment = new Comment();
        User user = userService.getAuthentication();
        comment.setName(user.getName());
        comment.setEmail(user.getEmail());
        comment.setComment(message);
        comment.setPost(postService.getPostById(postId));
        commentRepository.save(comment);
    }

    public List<Comment> getAllComment(UUID id) {
        return commentRepository.findAllByPostId(id);
    }

    public void updateComment(UUID commentId, String message) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        comment.setComment(message);
        commentRepository.save(comment);
    }
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }


    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
        }

    public boolean existsById(UUID commentId) {
        return commentRepository.existsById(commentId);
    }
}

