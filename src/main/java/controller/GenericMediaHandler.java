package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import models.Media;
import server.ResponseGenerator;
import service.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GenericMediaHandler<T extends Media> extends AuthenticatedHandler {

    private final MediaService<T> service;
    private final Class<T> clazz; // wegen Type Erasure wichtig
    private final String basePath; // movies, series
    private final ObjectMapper mapper = new ObjectMapper(); // Java <-> JSON
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    public GenericMediaHandler(MediaService<T> service, Class<T> clazz, String basePath) {
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

        if (path.matches("/" + basePath + "/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            T item = service.findById(id).orElse(null);

            if (item != null) {
                String response = mapper.writeValueAsString(item);
                responseGenerator.sendJsonResponse(exchange, 200, response);
            } else {
                responseGenerator.sendJsonError(exchange, 404, "Nicht gefunden");
            }

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
        String query = exchange.getRequestURI().getQuery(); // ?id=5
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            boolean deleted = service.deleteById(id);

            if (deleted) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
}
