package service;

import exception.CommentException;
import models.Comment;
import repository.CommentRepository;

import java.util.List;
import java.util.UUID;

public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /* ---------------------------------------------------
     * READ
     * --------------------------------------------------- */

    public List<Comment> getCommentsByMedia(UUID mediaId) {
        return commentRepository.findAllCommentsByMedia(mediaId);
    }

    public List<Comment> getCommentsByUser(UUID userId) {
        return commentRepository.findAllCommentsByUser(userId);
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */

    public boolean deleteComment(UUID mediaId, UUID currentUserId) {
        return commentRepository.deleteComment(mediaId, currentUserId);
    }

    /* ---------------------------------------------------
     * CREATE
     * --------------------------------------------------- */

    public boolean addNewComment(UUID mediaId, UUID currentUserId, String commentText) {
        if (commentText == null || commentText.isBlank()) {
            throw CommentException.invalidText();
        }
        if (commentText.length() > 255) {
            throw CommentException.textTooLong();
        }
        return commentRepository.addNewComment(mediaId, currentUserId, commentText);
    }

    /* ---------------------------------------------------
     * UPDATE
     * --------------------------------------------------- */

    public boolean confirmComment(UUID mediaId, UUID currentUserId) {
        return commentRepository.confirmComment(mediaId, currentUserId);
    }

    /* ---------------------------------------------------
     * HELPER / VALIDATION
     * --------------------------------------------------- */

    public boolean commentExistsByUserAndMedia(UUID currentUserId, UUID mediaId) {
        return commentRepository.commentExistsByUserAndMedia(mediaId, currentUserId);
    }
}
