package service;

import dto.LeaderboardUserDto;
import dto.MediaWithRatingDto;
import repository.MediaRepository;
import repository.UserRepository;

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
        System.out.println("FAV Genre: " + favoriteGenre);

        return mediaService.findReccommendedMedias(favoriteGenre, recommendationCount);
    }

}
