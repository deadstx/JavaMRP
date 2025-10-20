package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class LandingPage implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
           // case "POST" -> handlePost(exchange);
         //   case "DELETE" -> handleDelete(exchange);
            default -> {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
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
