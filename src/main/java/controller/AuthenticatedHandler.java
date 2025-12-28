package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResponseGenerator;
import service.AuthService;

import java.io.IOException;

public abstract class AuthenticatedHandler implements HttpHandler {

    protected final AuthService authService;
    protected final ResponseGenerator responseGenerator = new ResponseGenerator();

    protected AuthenticatedHandler(AuthService authService) {
        this.authService = authService;
    }

    protected boolean isAuthenticated(HttpExchange exchange) throws IOException {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        String token = null;

        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("token=")) {
                    token = cookie.substring("token=".length());
                    break;
                }
            }
        }

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
}
