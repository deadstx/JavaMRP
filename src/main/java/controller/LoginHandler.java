package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.*;
import server.ResponseGenerator;
import service.AuthService;

import java.io.IOException;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    public LoginHandler(AuthService authService) {
        this.authService = authService;
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

            if (username == null || password == null) {
                throw new BadRequestException();
            }

            if (!authService.isValidLogin(username, password)) {
                throw new UnauthorizedException();
            }

            String token = authService.generateToken(username);

            exchange.getResponseHeaders().add(
                    "Set-Cookie",
                    "token=" + token + "; HttpOnly; Path=/"
            );

            String response = String.format(
                    "{\"message\":\"Login erfolgreich\",\"user\":\"%s\"}",
                    username
            );

            responseGenerator.sendJsonResponse(exchange, 200, response);

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseGenerator.sendJsonError(exchange, new ServerErrorException());
        }
    }
}
