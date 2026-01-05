package service;

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
        System.out.println("getCommentsByMedia Service");
        return commentRepository.findAllCommentsByMedia(mediaId);
    }

    public List<Comment> getCommentsByUser(UUID userId) {
        System.out.println("getCommentsByUser Service");
        return commentRepository.findAllCommentsByUser(userId);
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */

    public boolean deleteComment(UUID mediaId, UUID currentUserId) {
        System.out.println("deleteComment Service");
        return commentRepository.deleteComment(mediaId, currentUserId);
    }

    /* ---------------------------------------------------
     * CREATE
     * --------------------------------------------------- */

    public boolean addNewComment(UUID mediaId, UUID currentUserId, String commentText) {
        System.out.println("addNewComment Service");
        if (commentText == null || commentText.isBlank()) {
            throw new IllegalArgumentException("Kommentar darf nicht leer sein.");
        }
        if (commentText.length() > 255) {
            throw new IllegalArgumentException("Kommentar darf maximal 255 Zeichen haben.");
        }
        return commentRepository.addNewComment(mediaId, currentUserId, commentText);
    }

    /* ---------------------------------------------------
     * UPDATE
     * --------------------------------------------------- */

    public boolean confirmComment(UUID mediaId, UUID currentUserId) {
        System.out.println("confirmComment Service");
        return commentRepository.confirmComment(mediaId, currentUserId);
    }

    /* ---------------------------------------------------
     * HELPER / VALIDATION
     * --------------------------------------------------- */

    public boolean commentExistsByUserAndMedia(UUID currentUserId, UUID mediaId) {
        List<Comment> comments = commentRepository.findAllCommentsByUser(currentUserId);
        return comments.stream().anyMatch(c -> c.getMediaId().equals(mediaId));
    }
}
