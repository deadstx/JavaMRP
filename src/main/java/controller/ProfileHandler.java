package controller;

import com.sun.net.httpserver.HttpExchange;
import server.ResponseGenerator;
import service.AuthService;

import java.io.IOException;

public class ProfileHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    // 🔑 WICHTIG
    public ProfileHandler(AuthService authService) {
        super(authService);
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
        responseGenerator.sendJsonResponse(exchange, 200, "GET USER TEST");
    }
}
