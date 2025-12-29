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
            case "POST" -> responseGenerator.sendJsonResponse(exchange, 200, "POST TEST");
            case "DELETE" -> responseGenerator.sendJsonResponse(exchange, 200, "DELETE TEST");
            default -> responseGenerator.sendJsonError(exchange, 405, "Method not allowed");
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        /*
         * GET /ratings/{ratingId}
         */
        if (path.matches("/ratings/" + UUID_REGEX)) {
            UUID ratingId = UUID.fromString(path.split("/")[2]);
            System.out.println("Rating ID im handler: " + ratingId);

            Rating rating = service.getRatingById(ratingId);

            if (rating == null) {
                responseGenerator.sendJsonError(exchange, 404, "Rating nicht gefunden");
                return;
            }
            // HIER KOMMT DER CODE NOCH HIN, also kommt eine antwort zurück
            try {
                String json = mapper.writeValueAsString(rating);
                responseGenerator.sendJsonResponse(exchange, 200, json);
            } catch (Exception e) {
                e.printStackTrace();
                responseGenerator.sendJsonError(exchange, 500, "Fehler beim Serialisieren");
            }

            return;
        }

        /*
         * GET /ratings/user/{userId}
         */
        if (path.matches("/ratings/user/" + UUID_REGEX)) {
            UUID userId = UUID.fromString(path.split("/")[3]);

            List<Rating> ratings = service.getRatingsByUser(userId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
            );
            return;
        }

        /*
         * GET /ratings/media/{mediaId}
         */
        if (path.matches("/ratings/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(path.split("/")[3]);

            List<Rating> ratings = service.getRatingsByMedia(mediaId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(ratings)
            );
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }
}
