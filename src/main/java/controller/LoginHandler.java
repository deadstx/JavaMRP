package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    private final AuthService authService = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStream is = exchange.getRequestBody();
            Map<String, String> credentials = mapper.readValue(is, Map.class);

            String username = credentials.get("username");
            String password = credentials.get("password");

            // nur test -> Datenbank einbinden
            if ("admin".equals(username) && "password".equals(password)) {
                String token = authService.generateToken(username);

                exchange.getResponseHeaders().add("Set-Cookie", "token=" + token + "; HttpOnly; Path=/");

                String response = "{\"message\": \"Login erfolgreich\"}";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(401, -1); // Unauthorized
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
