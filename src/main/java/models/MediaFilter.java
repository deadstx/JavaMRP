package models;

public enum MediaFilter {
    TITLE,
    GENRE,
    RELEASE_YEAR,
    AGE_RESTRICTION,
    MIN_RATING;

    public static MediaFilter fromString(String filter) {
        return switch (filter.toLowerCase()) {
            case "title" -> TITLE;
            case "genre" -> GENRE;
            case "release_year" -> RELEASE_YEAR;
            case "age_restriction" -> AGE_RESTRICTION;
            case "min_rating" -> MIN_RATING;
            default -> null; // unbekannter Filter
        };
    }
}
