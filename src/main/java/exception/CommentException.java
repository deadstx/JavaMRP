package exception;

public class CommentException extends ApiException {

    // --------------------------
    // Konstruktor
    // --------------------------
    public CommentException(int statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message);
    }

    // --------------------------
    // Factory-Methoden
    // --------------------------

    // Allgemeiner Validierungsfehler (z. B. leerer Text)
    public static CommentException invalidText() {
        return new CommentException(
                400,
                "INVALID_COMMENT",
                "Kommentartext darf nicht leer sein"
        );
    }

    public static CommentException textTooLong() {
        return new CommentException(
                400,
                "TEXT_TOO_LONG",
                "Kommentartext ist zu lang"
        );
    }

    // Kommentar existiert schon
    public static CommentException alreadyExists() {
        return new CommentException(
                409,
                "COMMENT_EXISTS",
                "Kommentar für dieses Medium existiert bereits"
        );
    }

    // Kommentar konnte nicht gelöscht werden
    public static CommentException deleteError() {
        return new CommentException(
                400,
                "DELETE_FAILED",
                "Kommentar konnte nicht gelöscht werden"
        );
    }

    // Kommentar konnte nicht erstellt werden
    public static CommentException insertError() {
        return new CommentException(
                500,
                "INSERT_FAILED",
                "Kommentar konnte nicht erstellt werden"
        );
    }

    // Kommentar konnte nicht bestätigt werden
    public static CommentException confirmError() {
        return new CommentException(
                400,
                "CONFIRM_FAILED",
                "Kommentar konnte nicht bestätigt werden"
        );
    }
}
