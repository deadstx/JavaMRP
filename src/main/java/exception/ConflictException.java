package exception;

public class ConflictException extends ApiException {

    // Default-Fall
    public ConflictException() {
        super(
                409,
                "ALREADY_EXISTS",
                "Eintrag existiert bereits"
        );
    }

    public ConflictException(String errorCode, String message) {
        super(
                409,
                errorCode,
                message
        );
    }
}
