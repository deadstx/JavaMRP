package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.UnauthorizedException;
import server.ResponseGenerator;
import service.AuthService;

import java.util.UUID;

public abstract class AuthenticatedHandler implements HttpHandler {

    protected final AuthService authService;
    protected final ResponseGenerator responseGenerator = new ResponseGenerator();

    protected AuthenticatedHandler(AuthService authService) {
        this.authService = authService;
    }


    protected boolean isAuthenticated(HttpExchange exchange) throws UnauthorizedException {
        String token = extractToken(exchange);

        if (token == null) {
            throw UnauthorizedException.missingToken();
        }

        try {
            authService.verifyToken(token);
            return true;
        } catch (UnauthorizedException e) {
            throw UnauthorizedException.invalidToken();
        }
    }


    protected UUID getCurrentUserId(HttpExchange exchange) throws UnauthorizedException {
        String token = extractToken(exchange);

        if (token == null) {
            throw UnauthorizedException.missingToken();
        }

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
