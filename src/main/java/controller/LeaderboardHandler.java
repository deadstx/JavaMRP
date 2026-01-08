package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import dto.LeaderboardUserDto;
import exception.*;
import models.User;
import server.ResponseGenerator;
import service.AuthService;
import service.LeaderboardService;
import service.RatingService;

import java.io.IOException;
import java.util.List;

public class LeaderboardHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LeaderboardService service;

    // UUID Regex
    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                    "[0-9a-fA-F]{12}";

    public LeaderboardHandler(AuthService authService, LeaderboardService service) {
        super(authService);
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Authentifizierung prüfen
            isAuthenticated(exchange);

            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGet(exchange);
                default -> throw new NotFoundException();

            }

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            ex.printStackTrace();
            ApiException internal = new ServerErrorException();
            responseGenerator.sendJsonError(exchange, internal);
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/leaderboard(/\\d+)?")) {
            int userCount = 5; // Standardfall

            String[] parts = path.split("/");
            if (parts.length == 5) { // "/ratings/users/history/X"
                try {
                    userCount = Integer.parseInt(parts[4]);
                    if (userCount > 100) userCount = 100;
                } catch (NumberFormatException e) {
                    userCount = 5; // Fallback
                }
            }

            List<LeaderboardUserDto> users = service.getTopUsers(userCount);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(users)
            );
            return;
        }


        throw new NotFoundException();
    }
}
