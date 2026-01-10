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
                "ALREADY_EXISTS",
                "Dieser Titel ist bereits vergeben"
        );
    }

    // Kommentar konnte nicht gelöscht werden
    public static MediaException deleteError() {
        return new MediaException(
                400,
                "DELETE_FAILED",
                "Medium konnte nicht gelöscht werden"
        );
    }

    // Kommentar konnte nicht gelöscht werden
    public static MediaException searchError() {
        return new MediaException(
                404,
                "SEARCH_ERROR",
                "Medium konnte nicht gefunden werden"
        );
    }

    // Kommentar konnte nicht erstellt werden
    public static MediaException updateError() {
        return new MediaException(
                500,
                "UPDATE_FAILED",
                "Daten konnten nicht bearbeitet werden"
        );
    }


}
