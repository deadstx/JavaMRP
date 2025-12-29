package controller;

import com.sun.net.httpserver.HttpExchange;
import models.User;
import server.ResponseGenerator;
import service.AuthService;
import service.MediaService;
import service.ProfileService;


import controller.AuthenticatedHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ProfileHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ProfileService service;

    public ProfileHandler(AuthService authService, ProfileService service) {
        super(authService);
        this.service = service;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isAuthenticated(exchange)) return;

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> responseGenerator.sendJsonResponse(exchange, 200, "POST USER TEST");
            case "DELETE" -> responseGenerator.sendJsonResponse(exchange, 200, "DELETE USER TEST");
            default -> responseGenerator.sendJsonResponse(exchange, 405, "Method not allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (!"/profile".equals(path)) {
            responseGenerator.sendJsonResponse(exchange, 404, "Not Found");
            return;
        }

        try {
            UUID currentUserId = getCurrentUserId(exchange);
            User userData = service.getProfileData(currentUserId);

            if (userData == null) {
                responseGenerator.sendJsonResponse(exchange, 404, "User not found");
                return;
            }

            responseGenerator.sendJsonResponse(exchange, 200, userData);

        } catch (IllegalArgumentException e) {
            responseGenerator.sendJsonResponse(exchange, 400, "Invalid user id");
        } catch (Exception e) {
            responseGenerator.sendJsonResponse(exchange, 500, "Internal server error");
        }
    }
}
