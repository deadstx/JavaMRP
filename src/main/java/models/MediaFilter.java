package models;

public enum MediaFilter {
    GENRE,
    RELEASE_YEAR,
    AGE_RESTRICTION,
    MIN_RATING;

    // Hilfsmethode, um Case-Insensitive vom String zu konvertieren
    public static MediaFilter fromString(String filter) {
        return switch (filter.toLowerCase()) {
            case "genre" -> GENRE;
            case "release_year" -> RELEASE_YEAR;
            case "age_restriction" -> AGE_RESTRICTION;
            case "min_rating" -> MIN_RATING;
            default -> null; // unbekannter Filter
        };
    }
}
