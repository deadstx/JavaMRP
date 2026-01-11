package service;

import dto.UserProfileDto;
import models.User;
import repository.ProfileRepository;

import java.util.UUID;

public class ProfileService {

    private final ProfileRepository profileRepository;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;

    public ProfileService(ProfileRepository profileRepository,
                          RatingService ratingService, FavoriteService favoriteService) {

        this.profileRepository = profileRepository;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
    }

    public UserProfileDto getProfileData(UUID userId) {
        User user = profileRepository.fetchUserProfile(userId);
        if (user == null) return null;

        int ratingCount = ratingService.getRatingCountByUser(userId);
        String favoriteGenre = favoriteService.getFavoriteGenre(userId);

        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                ratingCount,
                favoriteGenre
        );
    }
}
