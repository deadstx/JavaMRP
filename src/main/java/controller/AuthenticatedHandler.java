package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResponseGenerator;
import service.AuthService;

import java.io.IOException;
import java.util.UUID;

public abstract class AuthenticatedHandler implements HttpHandler {

    protected final AuthService authService;
    protected final ResponseGenerator responseGenerator = new ResponseGenerator();

    protected AuthenticatedHandler(AuthService authService) {
        this.authService = authService;
    }

    protected boolean isAuthenticated(HttpExchange exchange) throws IOException {
        String token = extractToken(exchange);

        if (token == null) {
            responseGenerator.sendJsonError(exchange, 401, "Nicht autorisiert – kein Token vorhanden");
            return false;
        }

        try {
            authService.verifyToken(token);
            return true;
        } catch (Exception e) {
            responseGenerator.sendJsonError(exchange, 401, "Ungültiger oder abgelaufener Token");
            return false;
        }
    }

    protected UUID getCurrentUserId(HttpExchange exchange) {
        String token = extractToken(exchange);
        System.out.println("TOKEN" + token);  // TEST OUTPUT
        return authService.getUserIdFromToken(token);
    }

    private String extractToken(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");

        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("token=")) {
                    return cookie.substring("token=".length());
                }
            }
        }
        return null;
    }
}
