package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import models.Media;
import server.ResponseGenerator;
import service.AuthService;
import service.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class GenericMediaHandler<T extends Media> extends AuthenticatedHandler {

    private final MediaService<T> service;
    private final Class<T> clazz;
    private final String basePath;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    // UUID Regex (einfach & ausreichend)
    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public GenericMediaHandler(
            AuthService authService,
            MediaService<T> service,
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

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // GET /movies/{uuid}
        if (path.matches("/" + basePath + "/" + UUID_REGEX)) {
            String idStr = path.split("/")[2];
            UUID id = UUID.fromString(idStr);

            T item = service.findById(id).orElse(null);

            if (item != null) {
                String response = mapper.writeValueAsString(item);
                responseGenerator.sendJsonResponse(exchange, 200, response);
            } else {
                responseGenerator.sendJsonError(exchange, 404, "Nicht gefunden");
            }

            // GET /movies
        } else if (("/" + basePath).equals(path)) {
            List<T> items = service.findAll();
            String response = mapper.writeValueAsString(items);
            responseGenerator.sendJsonResponse(exchange, 200, response);

        } else {
            responseGenerator.sendJsonError(exchange, 404, "Pfad ungültig");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream body = exchange.getRequestBody();
        T newItem = mapper.readValue(body, clazz);

        service.add(newItem);

        exchange.sendResponseHeaders(201, -1);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery(); // ?id=<uuid>

        if (query != null && query.startsWith("id=")) {
            try {
                UUID id = UUID.fromString(query.substring(3));
                boolean deleted = service.deleteById(id);

                if (deleted) {
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            } catch (IllegalArgumentException e) {
                exchange.sendResponseHeaders(400, -1); // ungültige UUID
            }
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
}
