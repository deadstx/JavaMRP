package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;



public class ResponseGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public void sendJsonResponse(HttpExchange exchange, int statusCode, Object body)
            throws IOException {

        String json = mapper.writeValueAsString(body);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }


    public void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, String> errorBody = Map.of("error", message);
        sendJsonResponse(exchange, statusCode, errorBody);
    }

}

