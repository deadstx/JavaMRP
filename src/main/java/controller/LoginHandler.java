package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResponseGenerator;
import service.AuthException;
import service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    private final AuthService authService = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        try (InputStream is = exchange.getRequestBody()) {
            Map<String, String> credentials = mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<>() {});

            String username = credentials.get("username");
            String password = credentials.get("password");

            try {
                if (authService.isValidLogin(username, password)) {
                    String token = authService.generateToken(username);
                    exchange.getResponseHeaders().add("Set-Cookie", "token=" + token + "; HttpOnly; Path=/");

                    String response = String.format("{\"message\": \"Login erfolgreich\", \"user\": \"%s\"}", username);
                    responseGenerator.sendJsonResponse(exchange, 200, response);

                } else {
                    String response = "{\"error\": \"Username oder Passwort sind falsch\"}";
                    responseGenerator.sendJsonError(exchange, 401, response);
                }

            } catch (AuthException e) {
                String response = String.format("{\"error\": \"Interner Authentifizierungsfehler: %s\"}", e.getMessage());
                responseGenerator.sendJsonError(exchange, 500, response);
            }

        } catch (Exception e) {
            String response = "{\"error\": \"Ungültige Anfrage oder JSON-Format\"}";
            responseGenerator.sendJsonError(exchange, 400, response);
        }
    }
}
