package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import models.Favorite;
import models.Media;
import server.ResponseGenerator;
import service.AuthService;
import service.FavoriteService;

import java.io.IOException;
import java.sql.SQLException;
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
        if (!isAuthenticated(exchange)) return;

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
          //  case "PUT" -> handleUpdate(exchange);
            default -> responseGenerator.sendJsonError(exchange, 405, "Method not allowed");
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        /*
         * GET /favorites/users/{userId}
         */
        if (path.matches("/favorites/users")) {
            UUID currentUser = getCurrentUserId(exchange);

            List<Media> favorites = service.getUserFavorites(currentUser);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(favorites)
            );
            return;
        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        UUID currentUserId = getCurrentUserId(exchange);

        // ========================
        // GET /media/{uuid}
        // ========================
        if (path.matches("/favorites/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(parts[3]);
            System.out.println("ID " + mediaId);

            if(service.exists(mediaId, currentUserId)) {
                responseGenerator.sendJsonError(exchange, 400, "Bereits als Favorit markiert");
                return;
            }

            if(service.markAsFavorite(mediaId, currentUserId)) {
                responseGenerator.sendJsonResponse(
                        exchange,
                        200,
                        "Als Favorit hinzugefügt"
                );
                return;
            }

            responseGenerator.sendJsonError(exchange, 500, "Server Error");

        }

        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }


    private void handleDelete(HttpExchange exchange) throws IOException{
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        UUID currentUserId = getCurrentUserId(exchange);

        if(path.matches("/favorites/media/" + UUID_REGEX)) {
            UUID mediaId = UUID.fromString(parts[3]);
            System.out.println("ID " + mediaId);

            if(service.exists(mediaId, currentUserId)) {
                if(service.removeFromFavorites(mediaId, currentUserId)) {
                    responseGenerator.sendJsonResponse(
                            exchange,
                            200,
                            "Aus Favoriten entfernt"
                    );
                    return;
                }
                responseGenerator.sendJsonError(exchange, 500, "Server Error");
            }
            responseGenerator.sendJsonError(exchange, 400, "Medium ist kein Favorit");
            return;
        }


        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }
}
