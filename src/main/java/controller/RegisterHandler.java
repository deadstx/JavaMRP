package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.*;
import server.ResponseGenerator;
import service.RegisterService;

import java.io.IOException;
import java.util.Map;

public class RegisterHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                throw new NotFoundException();
            }

            Map<String, String> credentials;
            try {
                credentials = mapper.readValue(
                        exchange.getRequestBody(),
                        new com.fasterxml.jackson.core.type.TypeReference<>() {}
                );
            } catch (Exception e) {
                throw new InvalidJsonException();
            }

            String username = credentials.get("username");
            String password = credentials.get("password");

            if (registerService.validateUsername(username) == false || registerService.validatePassword(password) == false) {
                throw new WrongInputException();
            }

            boolean success = registerService.registerUser(username, password);

            if (!success) {
                throw new ConflictException(); // Benutzername bereits vergeben
            }

            responseGenerator.sendJsonResponse(
                    exchange,
                    201,
                    "Account erfolgreich erstellt! Willkommen: " + username + "."
            );

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseGenerator.sendJsonError(exchange, new ServerErrorException());
        }
    }
}
