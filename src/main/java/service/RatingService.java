package service;

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
        System.out.println("getRatingById Service");
        System.out.println("Rating ID im Service: " + ratingId);
        return ratingRepository.findById(ratingId);
    }

    public List<Rating> getRatingsByUser(UUID userId) {
        System.out.println("getRatingByUser Service");
        return ratingRepository.findByUserId(userId);
    }

    public List<Rating> getRatingsByMedia(UUID mediaId) {
        System.out.println("getRatingByMedia Service");
        return ratingRepository.findByMediaId(mediaId);
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */

    public boolean deleteRating(UUID ratingId, UUID currentUserId) {
        return ratingRepository.deleteById(ratingId, currentUserId);
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

    private void validateStars(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Bewertung muss zwischen 1 und 5 Sternen liegen.");
        }
    }
}
