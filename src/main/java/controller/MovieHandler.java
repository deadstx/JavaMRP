package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Movie;
import service.AuthService;
import service.MovieService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class MovieHandler implements HttpHandler {

    private final MovieService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService = new AuthService();


    public MovieHandler(MovieService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {

        // Token aus Cookie extrahieren
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        String token = null;

        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("token=")) {
                    token = cookie.substring("token=".length());
                    break;
                }
            }
        }

        // Token prüfen
        if (token == null) {
            sendJsonError(exchange, 401, "Nicht autorisiert – kein Token vorhanden");
            return;
        }

        try {
            new AuthService().verifyToken(token); // throws Exception if invalid
        } catch (Exception e) {
            sendJsonError(exchange, 401, "Ungültiger oder abgelaufener Token");
            return;
        }

        // Path verarbeiten
        String path = exchange.getRequestURI().getPath(); // z. B. /movies oder /movies/123

        if (path.matches("/movies/\\d+")) {
            // Einzelner Film
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[2]);

            Movie movie = service.findMovieById(id).orElse(null);

            if (movie != null) {
                String response = mapper.writeValueAsString(movie);
                sendJsonResponse(exchange, 200, response);
            } else {
                sendJsonError(exchange, 404, "Dieser Film existiert nicht");
            }

        } else if ("/movies".equals(path)) {
            // Alle Filme
            List<Movie> movies = service.getAllMovies();
            String response = mapper.writeValueAsString(movies);
            sendJsonResponse(exchange, 200, response);

        } else {
            // Ungültiger Pfad
            sendJsonError(exchange, 404, "Pfad nicht gefunden");
        }
    }



    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream body = exchange.getRequestBody();
        Movie newMovie = mapper.readValue(body, Movie.class);
        service.addMovie(newMovie);

        exchange.sendResponseHeaders(201, -1); // Created
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery(); // e.g. ?id=5
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            boolean deleted = service.deleteMovieById(id);

            if (deleted) {
                exchange.sendResponseHeaders(200, -1); // OK
            } else {
                exchange.sendResponseHeaders(404, -1); // Not Found
            }
        } else {
            exchange.sendResponseHeaders(400, -1); // Bad Request
        }
    }



    // Hilfsfunktionen -> muss noch auslagern

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }

    private void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = mapper.writeValueAsString(Map.of("error", message));
        sendJsonResponse(exchange, statusCode, json);
    }


}
