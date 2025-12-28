package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResponseGenerator;
import service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    // 🔑 AuthService wird übergeben
    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (InputStream is = exchange.getRequestBody()) {

            Map<String, String> credentials =
                    mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<>() {});

            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                responseGenerator.sendJsonError(exchange, 400, "Username oder Passwort fehlen");
                return;
            }

            if (authService.isValidLogin(username, password)) {
                String token = authService.generateToken(username);

                exchange.getResponseHeaders().add(
                        "Set-Cookie",
                        "token=" + token + "; HttpOnly; Path=/"
                );

                String response =
                        String.format("{\"message\":\"Login erfolgreich\",\"user\":\"%s\"}", username);

                responseGenerator.sendJsonResponse(exchange, 200, response);

            } else {
                responseGenerator.sendJsonError(
                        exchange,
                        401,
                        "Username oder Passwort sind falsch"
                );
            }

        } catch (Exception e) {
            e.printStackTrace(); // 👈 DAS FEHLT
            responseGenerator.sendJsonError(
                    exchange,
                    400,
                    "Ungültige Anfrage oder JSON-Format"
            );
        }
    }
}
