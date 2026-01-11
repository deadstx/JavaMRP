package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import dto.MediaWithRatingDto;
import exception.*;
import server.ResponseGenerator;
import service.AuthService;
import service.RecommendationService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RecommendationHandler extends AuthenticatedHandler {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RecommendationService recommendationService;


    public RecommendationHandler(AuthService authService, RecommendationService recommendationService) {
        super(authService);
        this.recommendationService = recommendationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Auth prüfen
            isAuthenticated(exchange);

            if (exchange.getRequestMethod().equals("GET")) {
                handleGet(exchange);
            } else {
                throw new NotFoundException();
            }

        } catch (ApiException ex) {
            responseGenerator.sendJsonError(exchange, ex);

        } catch (Exception ex) {
            throw new ServerErrorException();
        }
    }

    /* ---------------------------------------------------
     * GET
     * --------------------------------------------------- */
    private void handleGet(HttpExchange exchange) throws IOException, ApiException {
        String path = exchange.getRequestURI().getPath();

        /*
         * GET /favorites/users
         */
        if (path.matches("/recommendations/media")) {
            int recommendationCount = 5;
            UUID currentUserId = getCurrentUserId(exchange);

            List<MediaWithRatingDto> recommendations = recommendationService.getUserRecommendations(currentUserId, recommendationCount);

            responseGenerator.sendJsonResponse(
                    exchange,
                    200,
                    mapper.writeValueAsString(recommendations)
            );
            return;
        }

        throw new NotFoundException();
    }
}
