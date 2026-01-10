package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import dto.MediaWithRatingDto;
import exception.*;
import models.Media;
import models.MediaFilter;
import models.MediaType;
import server.ResponseGenerator;
import service.AuthService;
import service.MediaService;

import java.io.IOException;
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
        try {
            isAuthenticated(exchange);

            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "PUT" -> handleUpdate(exchange);
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

    // ========================
    // GET
    // ========================
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        // GET /media/{uuid}
        if (path.matches("/" + basePath + "/" + UUID_REGEX)) {
            UUID id = UUID.fromString(parts[2]);

            MediaWithRatingDto media = service.findById(id);

            if (media == null) {
                throw new NotFoundException();
            }

            sendJson(exchange, 200, media);
            return;
        }


        // GET /media
        if (parts.length == 2) {
            List<MediaWithRatingDto> items = service.findAllWithRating();
            sendJson(exchange, 200, items);
            return;
        }

        // GET /media/filter/{filter}/{value}
        if (parts.length == 5 && "filter".equals(parts[2])) {
            MediaFilter filter = MediaFilter.fromString(parts[3]);
            if (filter == null) {
                throw new NotFoundException();
            }

            Object filterValue;

            try {
                filterValue = switch (filter) {
                    case TITLE, GENRE -> parts[4];
                    default -> Integer.parseInt(parts[4]);
                };
            } catch (NumberFormatException e) {
                throw new BadRequestException();
            }

            List<Media> result = service.filterMedia(filter, filterValue);

            sendJson(exchange, 200, result);
            return;
        }

        // GET /media/{type}
        if (parts.length == 3) {
            Optional<MediaType> mediaType = switch (parts[2].toLowerCase()) {
                case "movies" -> Optional.of(MediaType.movie);
                case "series" -> Optional.of(MediaType.series);
                case "games"  -> Optional.of(MediaType.game);
                default -> Optional.empty();
            };

            if (mediaType.isEmpty()) {
                throw new NotFoundException();
            }

            sendJson(exchange, 200, service.findAll(mediaType));
            return;
        }

        throw new NotFoundException();
    }

    // ========================
    // POST
    // ========================
    private void handlePost(HttpExchange exchange) throws IOException, ApiException {
        UUID currentUserId = getCurrentUserId(exchange);

        T media;
        try {
            media = mapper.readValue(exchange.getRequestBody(), clazz);
        } catch (Exception e) {
            throw new InvalidJsonException();
        }

        // === Eingaben prüfen ===
        if (media.getTitle() == null || media.getTitle().isBlank()) {
            throw  MediaException.invalidText("TITLE");
        }

        // === Titel prüfen ===
        if (service.existsByTitle(media.getTitle())) {
            throw MediaException.alreadyExists();
        }

        if (media.getDirector() == null || media.getDirector().isBlank()) {
            throw MediaException.invalidText("DIRECTOR");
        }
        if (media.getDescription() == null || media.getDescription().isBlank()) {
            throw MediaException.invalidText("DESCRIPTION");
        }
        if (media.getMediaType() == null) {
            throw MediaException.invalidText("MEDIA_TYPE");
        }
        if (media.getGenre() == null || media.getGenre().isBlank()) {
            throw MediaException.invalidText("GENRE");
        }
        if (media.getAgeRestriction() < 0) {
            throw MediaException.invalidAgeRestriction("AGE_RESTRICTION");
        }
        int currentYear = java.time.Year.now().getValue();
        if (media.getReleaseYear() < 1800 || media.getReleaseYear() > currentYear) {
            throw MediaException.invalidReleaseYear("RELEASE_YEAR");
        }

        // === Speichern ===
        boolean created = service.create(media, currentUserId);
        if (!created) {
            throw new ServerErrorException();
        }

        responseGenerator.sendJsonResponse(exchange, 201, "Media erstellt");
    }


    // ========================
    // PUT
    // ========================
    private void handleUpdate(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())
                || !path.matches("/" + basePath + "/" + UUID_REGEX)) {
            throw new BadRequestException();
        }

        UUID mediaId;
        try {
            mediaId = UUID.fromString(path.split("/")[2]);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }

        UUID currentUserId = getCurrentUserId(exchange);

        T media;
        try {
            media = mapper.readValue(exchange.getRequestBody(), clazz);
        } catch (Exception e) {
            throw new InvalidJsonException();
        }

        if (!service.existsById(mediaId)) {
            throw MediaException.searchError(); // 404
        }

        media.setId(mediaId);
        media.setCreatorId(currentUserId);

        boolean updated = service.update(media, currentUserId);
        if (!updated) {
            throw new ServerErrorException();
        }

        responseGenerator.sendJsonResponse(exchange, 204, "");
    }



    // ========================
    // DELETE
    // ========================
    private void handleDelete(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();
        UUID currentUserId = getCurrentUserId(exchange);

        if (!path.matches("/" + basePath + "/" + UUID_REGEX)) {
            throw new BadRequestException();
        }

        UUID id = UUID.fromString(path.split("/")[2]);


        boolean deleted = service.delete(id, currentUserId);
        if (!deleted) {
            throw new ServerErrorException();
        }

        responseGenerator.sendJsonResponse(exchange, 200, "Media gelöscht");
    }

    private void sendJson(HttpExchange exchange, int status, Object obj) throws IOException {
        responseGenerator.sendJsonResponse(
                exchange,
                status,
                mapper.writeValueAsString(obj)
        );
    }
}
