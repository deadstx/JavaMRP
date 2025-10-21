package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Movie;
import server.ResponseGenerator;
import service.AuthService;
import service.MovieService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MovieHandler extends AuthenticatedHandler {

    private final MovieService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService = new AuthService();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();


    public MovieHandler(MovieService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(!isAuthenticated(exchange)) return;

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

        // Path verarbeiten
        String path = exchange.getRequestURI().getPath(); // z. B. /movies oder /movies/123

        if (path.matches("/movies/\\d+")) {
            // Einzelner Film
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[2]);

            Movie movie = service.findMovieById(id).orElse(null);

            if (movie != null) {
                String response = mapper.writeValueAsString(movie);
                responseGenerator.sendJsonResponse(exchange, 200, response);
            } else {
                responseGenerator.sendJsonError(exchange, 404, "Dieser Film existiert nicht");
            }

        } else if ("/movies".equals(path)) {
            // Alle Filme
            List<Movie> movies = service.getAllMovies();
            String response = mapper.writeValueAsString(movies);
            responseGenerator.sendJsonResponse(exchange, 200, response);

        } else {
            // Ungültiger Pfad
            responseGenerator.sendJsonError(exchange, 404, "Pfad nicht gefunden");
        }
    }



    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream body = exchange.getRequestBody();
        Movie newMovie = mapper.readValue(body, Movie.class);
        service.addMovie(newMovie);

        exchange.sendResponseHeaders(201, -1); // Created
    }


    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery(); // zb ?id=5
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
}
