package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResponseGenerator;
import service.RegisterService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RegisterHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final RegisterService registerService;

    // Register Service wird übergeben
    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Nur POST-Anfragen erlauben
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (InputStream is = exchange.getRequestBody()) {

            Map<String, String> credentials = mapper.readValue(
                    is, new com.fasterxml.jackson.core.type.TypeReference<>() {}
            );

            String username = credentials.get("username");
            String password = credentials.get("password");

            // Prüfen, ob beide Werte vorhanden sind
            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                responseGenerator.sendJsonError(exchange, 400, "Username oder Passwort fehlen");
                return;
            }

            // Benutzer registrieren
            boolean success = registerService.registerUser(username, password);

            if (success) {
                // Registrierung erfolgreich
                responseGenerator.sendJsonResponse(exchange, 201, (
                        "Account erfolgreich erstellt! " +
                        "Wilkommen: " + username + "."
                ));
            } else {
                // Benutzer existiert bereits
                responseGenerator.sendJsonError(exchange, 409, "Dieser Benutzername ist nicht verfügbar!");
            }

        } catch (Exception e) {
            responseGenerator.sendJsonError(exchange, 400, "Ungültige Anfrage oder JSON-Format");
        }
    }
}
