package service;

import dto.LeaderboardUserDto;
import exception.WrongInputException;
import models.Rating;
import repository.RatingRepository;

import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /* ---------------------------------------------------
     * READ
     * --------------------------------------------------- */

    // spezifisches Rating suchen (mit ratingID)
    public Rating getRatingById(UUID ratingId) {
        return ratingRepository.findById(ratingId);
    }

    public List<Rating> getRatingsByUser(UUID userId) {
        return ratingRepository.findByUserId(userId);
    }

    public List<Rating> getRatingsByMedia(UUID mediaId) {
        return ratingRepository.findByMediaId(mediaId);
    }

    public List<Rating> getRatingHistory(UUID currentUserId, int ratingCount) {
        return ratingRepository.findRatingHistory(currentUserId, ratingCount);
    }

    public List<LeaderboardUserDto> getTopUserIdList(int userCount) {
        return ratingRepository.findTopUserList(userCount);
    }

    public int getRatingCountByUser(UUID userId) {
        return ratingRepository.findRatingCountByUser(userId);
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */

    public boolean deleteRating(UUID mediaId, UUID currentUserId) {
        return ratingRepository.deleteById(mediaId, currentUserId);
    }

    /* ---------------------------------------------------
     * POST
     * --------------------------------------------------- */

    public boolean addNewRating(Rating rating) {
        if(!validateStars(rating.getStars())) {
            throw new WrongInputException();
        }
        return ratingRepository.addNewRating(rating);
    }


    /* ---------------------------------------------------
     * HELPER / VALIDATION
     * --------------------------------------------------- */

    public boolean ratingExistsByMedia(UUID currentUserId, UUID mediaId) {
        return ratingRepository.existsByUserAndMedia(currentUserId, mediaId);
    }

    private boolean validateStars(int stars) {
        return stars >= 1 && stars <= 5;
    }
}
