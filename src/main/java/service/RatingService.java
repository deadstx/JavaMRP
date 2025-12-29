package service;

import models.Rating;
import repository.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /* ---------------------------------------------------
     * CREATE
     * --------------------------------------------------- */

    public Rating createRating(UUID userId, UUID mediaId, int stars, String comment, String created_at) {

        // 1. Validierung
        validateStars(stars);

        // 2. Prüfen, ob User bereits bewertet hat
        if (ratingRepository.existsByUserAndMedia(userId, mediaId)) {
            throw new IllegalStateException("User hat dieses Medium bereits bewertet.");
        }

        // 3. Rating erzeugen
        Rating rating = new Rating(
                UUID.randomUUID(),
                userId,
                mediaId,
                stars,
                comment,
                created_at
        );

        // 4. Speichern
        ratingRepository.save(rating);

        return rating;
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

    public void deleteRating(UUID ratingId, UUID currentUserId) {
        // Repository stellt bereits sicher, dass nur eigene Ratings gelöscht werden
        ratingRepository.deleteById(ratingId, currentUserId);
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
