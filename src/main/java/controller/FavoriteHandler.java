package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
import models.Media;
import server.ResponseGenerator;
import service.AuthService;
import service.FavoriteService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FavoriteHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final FavoriteService service;

    // UUID Regex
    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public FavoriteHandler(AuthService authService, FavoriteService service) {
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

        /*
         * GET /favorites/users
         */
        if (path.matches("/favorites/users")) {
            UUID currentUserId = getCurrentUserId(exchange);

            List<Media> favorites = service.getUserFavorites(currentUserId);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(favorites)
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

        /*
         * POST /favorites/media/{mediaId}
         */
        if (!path.matches("/favorites/media/" + UUID_REGEX)) {
            throw new NotFoundException();
        }

        UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
        UUID userId = getCurrentUserId(exchange);

        if (service.exists(mediaId, userId)) {
            throw new ConflictException(); // bereits Favorit
        }

        boolean success = service.markAsFavorite(mediaId, userId);
        if (success) {
            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    "Als Favorit hinzugefügt"
            );
        } else {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */
    private void handleDelete(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        /*
         * DELETE /favorites/media/{mediaId}
         */
        if (!path.matches("/favorites/media/" + UUID_REGEX)) {
            throw new NotFoundException();
        }

        UUID mediaId = UUID.fromString(path.substring(path.lastIndexOf("/") + 1));
        UUID userId = getCurrentUserId(exchange);

        if (!service.exists(mediaId, userId)) {
            throw new BadRequestException(); // kein Favorit
        }

        boolean success = service.removeFromFavorites(mediaId, userId);
        if (success) {
            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    "Aus Favoriten entfernt"
            );
        } else {
            throw new ServerErrorException();
        }
    }
}
