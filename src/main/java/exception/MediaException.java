package exception;

public class MediaException extends ApiException {

    public MediaException(int statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message);
    }

    public static MediaException invalidText(String text) {
        return new MediaException(
                400,
                "INVALID_" +text,
                text + " darf nicht leer sein"
        );
    }

    public static MediaException invalidAgeRestriction(String text) {
        return new MediaException(
                400,
                "INVALID_" +text,
                text + " muss > 0 sein"
        );
    }

    public static MediaException invalidReleaseYear(String text) {
        return new MediaException(
                400,
                "INVALID_" +text,
                text + " muss in der Vergangenheit liegen"
        );
    }

    // Kommentar existiert schon
    public static MediaException alreadyExists() {
        return new MediaException(
                409,
                "COMMENT_EXISTS",
                "Kommentar für dieses Medium existiert bereits"
        );
    }

    // Kommentar konnte nicht gelöscht werden
    public static MediaException deleteError() {
        return new MediaException(
                400,
                "DELETE_FAILED",
                "Kommentar konnte nicht gelöscht werden"
        );
    }

    // Kommentar konnte nicht erstellt werden
    public static MediaException insertError() {
        return new MediaException(
                500,
                "INSERT_FAILED",
                "Kommentar konnte nicht erstellt werden"
        );
    }

    // Kommentar konnte nicht bestätigt werden
    public static MediaException confirmError() {
        return new MediaException(
                400,
                "CONFIRM_FAILED",
                "Kommentar konnte nicht bestätigt werden"
        );
    }
}
