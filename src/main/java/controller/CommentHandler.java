package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
import models.Comment;
import server.ResponseGenerator;
import service.AuthService;
import service.CommentService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CommentHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final CommentService service;

    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public CommentHandler(AuthService authService, CommentService service) {
        super(authService);
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            isAuthenticated(exchange);

            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                case "PUT" -> handleConfirm(exchange);
                default -> throw new NotFoundException();

            }

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/comments/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));

            List<Comment> comments = service.getCommentsByMedia(mediaId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(comments)
            );
            return;
        }

        if (path.matches("/comments/users")) {
            UUID currentUserId = getCurrentUserId(exchange);

            List<Comment> comments = service.getCommentsByUser(currentUserId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(comments)
            );
            return;
        }

        throw new NotFoundException();
    }

    /* ---------------------------------------------------
     * POST
     * --------------------------------------------------- */
    private void handlePost(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (!path.matches("/comments/media/" + UUID_REGEX)) {
            throw new NotFoundException();
        }

        UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
        UUID userId = getCurrentUserId(exchange);

        String body = new String(exchange.getRequestBody().readAllBytes()).trim();
        if (body.isEmpty()) {
            throw new BadRequestException();
        }

        Comment comment;
        try {
            comment = mapper.readValue(body, Comment.class);
        } catch (Exception e) {
            throw new InvalidJsonException();
        }

        if (comment.getComment_text() == null || comment.getComment_text().isBlank()) {
            throw CommentException.invalidText();
        }

        comment.setUserId(userId);
        comment.setMediaId(mediaId);

        if (service.commentExistsByUserAndMedia(userId, mediaId)) {
            throw new ConflictException();
        }

        boolean success = service.addNewComment(mediaId, userId, comment.getComment_text());
        if (success) {
            responseGenerator.sendJsonResponse(exchange, 201, "Kommentar erstellt");
        } else {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */
    private void handleDelete(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/comments/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.deleteComment(mediaId, userId);
            if (success) {
                responseGenerator.sendJsonResponse(exchange, 200, "Kommentar gelöscht!");
            } else {
                throw CommentException.deleteError();
            }
            return;
        }

        throw new NotFoundException();
    }

    /* ---------------------------------------------------
     * PUT (CONFIRM)
     * --------------------------------------------------- */
    private void handleConfirm(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/comments/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.confirmComment(mediaId, userId);
            if (success) {
                responseGenerator.sendJsonResponse(exchange, 200, "Kommentar bestätigt!");
            } else {
                throw CommentException.confirmError();
            }
            return;
        }

        throw new NotFoundException();
    }
}
