package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import dto.MediaWithRatingDto;
import models.Media;
import models.MediaType;
import models.MediaFilter;
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
            case "PUT" -> handleUpdate(exchange);
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
        // GET /media/{media_id} -> EIN SPEZIFISCHES
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
        // GET /media -> ALLE EINTRÄGE
        // ========================
        if (parts.length == 2) { // ["", "media"]
            List<MediaWithRatingDto> items = service.findAllWithRating();
            sendJson(exchange, 200, items);
            return;
        }

        // ========================
        // GET /media/filter/{filter}
        // Filterparameter: genres, release_year, age_restriction
        // ========================

        // Angenommen: parts = requestPath.split("/")
        if (parts.length == 5 && "filter".equals(parts[2])) {
            String filterStr = parts[3]; // filtername
            String value = parts[4];     // filter value

            MediaFilter filter = MediaFilter.fromString(filterStr);

            if (filter == null) {
                responseGenerator.sendJsonError(exchange, 404, "Filter ungültig");
                return;
            }

            List<Media> result = List.of();

            switch (filter) {
                case GENRE -> result = service.filterMediaByGenre(value);
                case RELEASE_YEAR -> {
                    int year;
                    try {
                        year = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        responseGenerator.sendJsonError(exchange, 400, "Must be year");
                        return;
                    }
                    result = service.filterMediaByReleaseYear(year);
                }
                case AGE_RESTRICTION -> {
                    int age;
                    try {
                        age = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        responseGenerator.sendJsonError(exchange, 400, "Must be a number");
                        return;
                    }
                    result = service.filterMediaByAgeRestriction(age);
                }
            }

            sendJson(exchange, 200, result);


        }

        // ========================
        // GET /media/{type}
        // ========================
        if (parts.length == 3) { // ["", "media", "movies"]
            String type = parts[2].toLowerCase();

            Optional<MediaType> media_type = switch (type) {
                case "movies" -> Optional.of(MediaType.movie);
                case "series" -> Optional.of(MediaType.series);
                case "games"  -> Optional.of(MediaType.game);
                default -> Optional.empty();
            };

            if (media_type.isEmpty()) {
                responseGenerator.sendJsonError(exchange, 404, "Unbekannter Media-Typ");
                return;
            }

            List<Media> items = service.findAll(media_type);
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

        try {
            // 1️⃣ Debug Print: Handler startet
            System.out.println("HANDLER STARTET POST");
            System.out.flush();

            // 2️⃣ URI / Path ausgeben
            String path = exchange.getRequestURI().getPath();
            System.out.println("PATH: " + path);
            System.out.flush();

            // 3️⃣ Request Body lesen und in Media-Objekt mappen
            InputStream bodyStream = exchange.getRequestBody();
            if (bodyStream == null) {
                System.out.println("KEIN BODY GELESEN!");
                exchange.sendResponseHeaders(400, -1); // Bad Request
                return;
            }

            T media;
            try {
                media = mapper.readValue(bodyStream, clazz);
            } catch (Exception e) {
                System.err.println("FEHLER beim Parsen des Bodys:");
                e.printStackTrace();
                exchange.sendResponseHeaders(400, -1); // Bad Request
                return;
            }

            System.out.println("MEDIA GELADEN: " + media);
            System.out.flush();

            // 4️⃣ Media speichern
            boolean saved = service.save(media, currentUserId);
            if (!saved) {
                System.out.println("SPEICHERN NICHT ERLAUBT!");
                exchange.sendResponseHeaders(403, -1); // Forbidden
                return;
            }

            // 5️⃣ Erfolgreiche Response
            exchange.sendResponseHeaders(201, -1); // Created
            System.out.println("MEDIA ERFOLGREICH GESPEICHERT");
            System.out.flush();

        } catch (Exception e) {
            // 6️⃣ Allgemeine Fehlerbehandlung
            System.err.println("UNERWARTETER FEHLER IN HANDLEPOST:");
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        } finally {
            exchange.close(); // Stream immer schließen
        }
    }


    // ========================
    // UPDATE
    // ========================
    private void handleUpdate(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        UUID currentUserId = getCurrentUserId(exchange);

        // PUT /media/{uuid}
        if (!path.matches("/" + basePath + "/" + UUID_REGEX)) {
            responseGenerator.sendJsonError(exchange, 400, "Ungültiger Pfad");
            return;
        }

        UUID id = UUID.fromString(path.split("/")[2]);

        // Body lesen
        T updatedMedia = mapper.readValue(exchange.getRequestBody(), clazz);

        // ID aus URL erzwingen (Sicherheitsmaßnahme)
        updatedMedia.setId(id);

        boolean updated = service.save(updatedMedia, currentUserId);

        if (!updated) {
            responseGenerator.sendJsonError(exchange, 403, "Keine Berechtigung");
            return;
        }

        exchange.sendResponseHeaders(204, -1); // No Content
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
