package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
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

    // UUID Regex
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
        if (!isAuthenticated(exchange)) return;

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            case "PUT" -> handleConfirm(exchange);
            default -> responseGenerator.sendJsonError(exchange, 405, "Method not allowed");
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        //comments/media/{mediaId} -> ALLE KOMMENTARE ZU EINEM BESTIMMTEN MEDIUM (NUR BESTÄTIGTE WERDEN GEZEIGT)
        if (path.matches("/comments/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            System.out.println("MEDIA ID: " + mediaId);

            List<Comment> comments = service.getCommentsByMedia(mediaId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(comments)
            );
            return;
        }

        //comments/users -> ALLE KOMMENTARE DIE MAN SELBST GESCHRIEBEN HAT (AUCH NICHT BESTÄTIGTE WERDEN GEZEIGT)
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

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }

    /* ---------------------------------------------------
     * POST
     * --------------------------------------------------- */

    private void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // POST /comments/media/{mediaId}
        if (!path.matches("/comments/media/" + UUID_REGEX)) {
            responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
            return;
        }

        UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
        UUID userId = getCurrentUserId(exchange);

        // ---------- Body lesen ----------
        String body = new String(exchange.getRequestBody().readAllBytes()).trim();

        if (body.isEmpty()) {
            responseGenerator.sendJsonError(
                    exchange,
                    400,
                    "Request-Body darf nicht leer sein"
            );
            return;
        }

        Comment comment;
        try {
            comment = mapper.readValue(body, Comment.class);
        } catch (Exception e) {
            responseGenerator.sendJsonError(
                    exchange,
                    400,
                    "Ungültiges JSON-Format"
            );
            return;
        }

        // ---------- Validierung ----------
        if (comment.getComment_text() == null || comment.getComment_text().isBlank()) {
            responseGenerator.sendJsonError(
                    exchange,
                    400,
                    "Kommentartext darf nicht leer sein"
            );
            return;
        }

        // ---------- Serverseitig setzen ----------
        comment.setUserId(userId);
        comment.setMediaId(mediaId);

        // ---------- Existenzprüfung ----------
        if (service.commentExistsByUserAndMedia(userId, mediaId)) {
            responseGenerator.sendJsonError(
                    exchange,
                    409,
                    "Kommentar für dieses Medium existiert bereits"
            );
            return;
        }

        boolean success = service.addNewComment(
                mediaId,
                userId,
                comment.getComment_text()
        );

        if (success) {
            responseGenerator.sendJsonResponse(
                    exchange,
                    201,
                    "Kommentar erstellt"
            );
        } else {
            responseGenerator.sendJsonError(
                    exchange,
                    500,
                    "Kommentar konnte nicht erstellt werden"
            );
        }
    }


    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // DELETE /comments/media/{mediaId}
        if (path.matches("/comments/media/" + UUID_REGEX)) {

            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.deleteComment(mediaId, userId);

            if (success) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        200,
                        "Kommentar gelöscht!"
                );
            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        400,
                        "Kommentar konnte nicht gelöscht werden."
                );
            }
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }

    /* ---------------------------------------------------
     * PUT
     * --------------------------------------------------- */

    private void handleConfirm(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // PUT /comments/media/{mediaId}
        if (path.matches("/comments/media/" + UUID_REGEX)) {

            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.confirmComment(mediaId, userId);

            if (success) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        200,
                        "Kommentar bestätigt!"
                );
            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        400,
                        "Kommentar konnte nicht bestätigt werden."
                );
            }
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig! Versuche /comments/media/{media_id}");
    }
}
