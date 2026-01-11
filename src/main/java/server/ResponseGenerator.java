package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.ApiException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    // -------------------------------
    // Error Response aus Exception
    // -------------------------------
    public void sendJsonError(HttpExchange exchange, ApiException ex) throws IOException {
        Map<String, String> errorBody = Map.of(
                "errorCode", ex.getErrorCode(),
                "message", ex.getMessage()
        );
        sendJsonResponse(exchange, ex.getHttpStatus(), errorBody);
    }

    // -------------------------------
    // Success Response
    // -------------------------------
    public void sendJsonResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
        String json = mapper.writeValueAsString(body);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
