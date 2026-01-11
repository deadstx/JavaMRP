package controller;

import com.sun.net.httpserver.HttpExchange;
import dto.UserProfileDto;
import exception.*;
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
        try {
            isAuthenticated(exchange);

            if (exchange.getRequestMethod().equals("GET")) {
                handleGet(exchange);
            } else {
                throw new NotFoundException();
            }

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        // /profile/users/{uuid} (bestimmter User)
        UUID userId = extractUserIdFromPath(path);
        if (userId != null) {
            sendUserProfile(exchange, userId);
            return;
        }

        // /profile/users (aktueller User)
        if ("/profile/users".equals(path)) {
            UUID currentUserId = getCurrentUserId(exchange);
            sendUserProfile(exchange, currentUserId);
            return;
        }

        throw new NotFoundException();
    }

    private UUID extractUserIdFromPath(String path) throws BadRequestException {
        Matcher matcher = USER_ID_PATTERN.matcher(path);
        if (!matcher.matches()) {
            return null;
        }

        try {
            return UUID.fromString(matcher.group(1));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    private void sendUserProfile(HttpExchange exchange, UUID userId)
            throws IOException, ApiException {

        UserProfileDto profile = profileService.getProfileData(userId);

        if (profile == null) {
            throw new NotFoundException();
        }

        responseGenerator.sendJsonResponse(exchange, 200, profile);
    }
}
