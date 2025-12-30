package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import models.Media;
import models.MediaType;
import server.ResponseGenerator;
import service.AuthService;
import service.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GenericMediaHandler<T extends Media> extends AuthenticatedHandler {

    private final MediaService service;
    private final Class<T> clazz;
    private final String basePath;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public GenericMediaHandler(
            AuthService authService,
            MediaService service,
            Class<T> clazz,
            String basePath
    ) {
        super(authService);
        this.service = service;
        this.clazz = clazz;
        this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isAuthenticated(exchange)) return;

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> exchange.sendResponseHeaders(405, -1);
        }
    }

    // ========================
    // GET
    // ========================
    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        // ========================
        // GET /media/{uuid}
        // ========================
        if (path.matches("/" + basePath + "/" + UUID_REGEX)) {
            UUID id = UUID.fromString(parts[2]);

            service.findById(id)
                    .ifPresentOrElse(
                            media -> {
                                try {
                                    sendJson(exchange, 200, media);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                try {
                                    responseGenerator.sendJsonError(exchange, 404, "Nicht gefunden");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
            return;
        }

        // ========================
        // GET /media
        // ========================
        if (parts.length == 2) { // ["", "media"]
            List<Media> items = service.findAll();
            sendJson(exchange, 200, items);
            return;
        }

        // ========================
        // GET /media/{type}
        // ========================
        if (parts.length == 3) { // ["", "media", "movies"]
            String type = parts[2].toLowerCase();

            Optional<MediaType> mediaType = switch (type) {
                case "movies" -> Optional.of(MediaType.MOVIE);
                case "series" -> Optional.of(MediaType.SERIES);
                case "games"  -> Optional.of(MediaType.GAME);
                default -> Optional.empty();
            };

            if (mediaType.isEmpty()) {
                responseGenerator.sendJsonError(exchange, 404, "Unbekannter Media-Typ");
                return;
            }

            List<Media> items = service.findAll(mediaType);
            sendJson(exchange, 200, items);
            return;
        }

        // ========================
        // Fallback
        // ========================
        responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
    }


    // ========================
    // POST
    // ========================
    private void handlePost(HttpExchange exchange) throws IOException {
        UUID currentUserId = getCurrentUserId(exchange);

        InputStream body = exchange.getRequestBody();
        T media = mapper.readValue(body, clazz);

        service.save(media, currentUserId);

        exchange.sendResponseHeaders(201, -1);
    }

    // ========================
    // DELETE
    // ========================
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        UUID currentUserId = getCurrentUserId(exchange);

        if (path.matches("/" + basePath + "/" + UUID_REGEX)) {
            UUID id = UUID.fromString(path.split("/")[2]);

            boolean deleted = service.delete(id, currentUserId);

            exchange.sendResponseHeaders(deleted ? 200 : 404, -1);
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }

    private void sendJson(HttpExchange exchange, int status, Object obj) throws IOException {
        String json = mapper.writeValueAsString(obj);
        responseGenerator.sendJsonResponse(exchange, status, json);
    }
}
