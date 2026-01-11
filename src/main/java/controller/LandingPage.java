package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class LandingPage implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if(!Objects.equals(exchange.getRequestMethod(), "GET")) {
            throw new NotFoundException();
        } else {
            handleGet(exchange);
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {

        String response = "\"Willkommen bei meinem MRP Projekt!\"";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
