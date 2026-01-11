package service;

import dto.MediaWithRatingDto;

import java.util.List;
import java.util.UUID;


public class RecommendationService {

    private final MediaService mediaService;
    private final FavoriteService favoriteService;


    public RecommendationService(MediaService mediaService, FavoriteService favoriteService) {
        this.mediaService = mediaService;
        this.favoriteService = favoriteService;
    }

    /* ---------------------------------------------------
     * READ
     * --------------------------------------------------- */

    public List<MediaWithRatingDto> getUserRecommendations(UUID currentUserId, int recommendationCount) {
        String favoriteGenre = favoriteService.getFavoriteGenre(currentUserId);

        return mediaService.findRecommendedMedias(favoriteGenre, recommendationCount);
    }

}
