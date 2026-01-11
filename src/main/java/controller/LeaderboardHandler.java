package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import dto.LeaderboardUserDto;
import exception.*;
import server.ResponseGenerator;
import service.AuthService;
import service.LeaderboardService;

import java.io.IOException;
import java.util.List;

public class LeaderboardHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LeaderboardService service;

    public LeaderboardHandler(AuthService authService, LeaderboardService service) {
        super(authService);
        this.service = service;
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

        if (path.matches("/leaderboard(/\\d+)?")) {
            int userCount = 5; // standardfall wenn nichts angegeben

            String[] parts = path.split("/");
            if (parts.length == 5) { // "/ratings/users/history/X"
                try {
                    userCount = Integer.parseInt(parts[4]);
                    if (userCount > 50) userCount = 50;
                } catch (NumberFormatException e) {
                    userCount = 5;
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
