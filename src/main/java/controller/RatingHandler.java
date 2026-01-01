package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import models.Rating;
import server.ResponseGenerator;
import service.AuthService;
import service.RatingService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RatingHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RatingService service;

    // UUID Regex
    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public RatingHandler(AuthService authService, RatingService service) {
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
            case "UPDATE" -> handleUpdate(exchange);
            default -> responseGenerator.sendJsonError(exchange, 405, "Method not allowed");
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        /*
         * GET /ratings/media/{mediaId}
         */
        if (path.matches("/ratings/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));

            List<Rating> ratings = service.getRatingsByMedia(mediaId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
            );
            return;
        }

        /*
         * GET /ratings/users/{userId}
         */
        if (path.matches("/ratings/users/" + UUID_REGEX)) {
            UUID userId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));

            List<Rating> ratings = service.getRatingsByUser(userId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
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

        /*
         * POST /ratings/media/{mediaId}
         */
        if (path.matches("/ratings/media/" + UUID_REGEX)) {

            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            // Request-Body lesen
            String body = new String(exchange.getRequestBody().readAllBytes());

            // JSON → Rating
            Rating rating = mapper.readValue(body, Rating.class);

            // Server setzt sichere Felder
            rating.initNewRating(userId, mediaId);

            boolean success = service.addNewRating(rating);

            if (success) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        201,
                        "Rating erstellt!"
                );
            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        400,
                        "Rating konnte nicht erstellt werden"
                );
            }
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }



    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        /*
         * DELETE /ratings/media/{mediaId}
         */
        if (path.matches("/ratings/media/" + UUID_REGEX)) {

            UUID ratingId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.deleteRating(ratingId, userId);

            if (success) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        201,
                        "Rating gelöscht!"
                );
            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        400,
                        "Rating konnte nicht gelöscht werden."
                );
            }
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }


    private void handleUpdate(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        /*
         * POST /ratings/media/{ratingId}
         */
        if (path.matches("/ratings/media/" + UUID_REGEX)) {

            UUID ratingId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.updateRatingStatus(ratingId, userId);

            if (success) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        201,
                        "Rating Confirmed!"
                );
            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        400,
                        "Rating konnte nicht bearbeitet werden"
                );
            }
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }
}
