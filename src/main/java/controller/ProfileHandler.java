package controller;

import com.sun.net.httpserver.HttpExchange;
import dto.UserProfileDto;
import server.ResponseGenerator;
import service.AuthService;
import service.ProfileService;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ProfileService profileService;

    private static final Pattern USER_ID_PATTERN =
            Pattern.compile("/profile/users/([0-9a-fA-F\\-]{36})");

    public ProfileHandler(AuthService authService, ProfileService profileService) {
        super(authService);
        this.profileService = profileService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isAuthenticated(exchange)) {
            return;
        }

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            default -> responseGenerator.sendJsonResponse(
                    exchange,
                    405,
                    "Method not allowed"
            );
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        UUID userId = extractUserIdFromPath(path);

        if (userId != null) {
            // /profile/users/{uuid}
            sendUserProfile(exchange, userId);
            return;
        }

        if ("/profile/users".equals(path)) {
            // /profile/users → aktueller Benutzer
            UUID currentUserId = getCurrentUserId(exchange);
            sendUserProfile(exchange, currentUserId);
            return;
        }

        responseGenerator.sendJsonResponse(exchange, 404, "Endpoint not found");
    }

    private UUID extractUserIdFromPath(String path) {
        Matcher matcher = USER_ID_PATTERN.matcher(path);
        if (!matcher.matches()) {
            return null;
        }

        try {
            return UUID.fromString(matcher.group(1));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void sendUserProfile(HttpExchange exchange, UUID userId) throws IOException {
        try {
            UserProfileDto profile = profileService.getProfileData(userId);

            if (profile == null) {
                responseGenerator.sendJsonResponse(exchange, 404, "User not found");
                return;
            }

            responseGenerator.sendJsonResponse(exchange, 200, profile);

        } catch (IllegalArgumentException e) {
            responseGenerator.sendJsonResponse(exchange, 400, "Invalid user id");
        } catch (Exception e) {
            e.printStackTrace();
            responseGenerator.sendJsonResponse(exchange, 500, "Internal server error");
        }
    }
}
