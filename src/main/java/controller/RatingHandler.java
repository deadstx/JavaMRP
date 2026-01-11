package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
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
        try {
            // Auth prüfen
            isAuthenticated(exchange);

            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> throw new NotFoundException();
            }

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseGenerator.sendJsonError(exchange, new ServerErrorException());
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

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

        if (path.matches("/ratings/users")) {
            UUID currentUserId = getCurrentUserId(exchange);

            List<Rating> ratings = service.getRatingsByUser(currentUserId);
            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
            );
            return;
        }

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

        if (path.matches("/ratings/users/history(/\\d+)?")) {
            UUID currentUserId = getCurrentUserId(exchange);
            int ratingCount = 5; // Standardfall

            String[] parts = path.split("/");
            if (parts.length == 5) { // "/ratings/users/history/X"
                try {
                    ratingCount = Integer.parseInt(parts[4]);
                } catch (NumberFormatException e) {
                    ratingCount = 5; // Fallback
                }
            }

            List<Rating> ratings = service.getRatingHistory(currentUserId, ratingCount);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
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

        if (!path.matches("/ratings/media/" + UUID_REGEX)) {
            throw new NotFoundException();
        }

        UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
        UUID userId = getCurrentUserId(exchange);

        String body = new String(exchange.getRequestBody().readAllBytes()).trim();
        if (body.isEmpty()) {
            throw new BadRequestException();
        }

        Rating rating;
        try {
            rating = mapper.readValue(body, Rating.class);
        } catch (Exception e) {
            throw new InvalidJsonException();
        }

        rating.initNewRating(userId, mediaId);

        if (service.ratingExistsByMedia(userId, mediaId)) {
            throw new ConflictException();
        }

        boolean success = service.addNewRating(rating);
        if (success) {
            responseGenerator.sendJsonResponse(exchange, 201, "Rating erstellt!");
        } else {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */
    private void handleDelete(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/ratings/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
            UUID userId = getCurrentUserId(exchange);

            boolean success = service.deleteRating(mediaId, userId);
            if (success) {
                responseGenerator.sendJsonResponse(exchange, 200, "Rating gelöscht!");
            } else {
                throw new ServerErrorException();
            }
            return;
        }

        throw new NotFoundException();
    }

}
