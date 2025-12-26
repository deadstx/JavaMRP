package controller;

import com.sun.net.httpserver.HttpExchange;
import server.ResponseGenerator;

import java.io.IOException;

public class ProfileHandler extends AuthenticatedHandler{

    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isAuthenticated(exchange)) return;

        String response = "";

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                response = "POST USER TEST";
                responseGenerator.sendJsonResponse(exchange, 200, response);
                break;
            case "DELETE":
                response = "DELETE USER TEST";
                responseGenerator.sendJsonResponse(exchange, 200, response);
                break;
            default:
                System.out.println("other route");
                responseGenerator.sendJsonResponse(exchange, 405, "Method not allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String response = "GET USER TEST";
        responseGenerator.sendJsonResponse(exchange, 200, response);
    }
}
