package service;

import dto.LeaderboardUserDto;
import models.Rating;
import repository.RatingRepository;

import java.io.IOException;
import java.time.LocalDateTime;
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
        return ratingRepository.addNewRating(rating);
    }


    /* ---------------------------------------------------
     * UPDATE
     * --------------------------------------------------- */

    public boolean updateRatingStatus(UUID ratingId, UUID currentUserId) {
        return ratingRepository.updateRatingStatus(ratingId, currentUserId);
    }


    /* ---------------------------------------------------
     * HELPER / VALIDATION
     * --------------------------------------------------- */

    public boolean ratingExistsByMedia(UUID currentUserId, UUID mediaId) {
        return ratingRepository.existsByUserAndMedia(currentUserId, mediaId);
    }

    private void validateStars(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Bewertung muss zwischen 1 und 5 Sternen liegen.");
        }
    }
}
