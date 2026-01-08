package exception;

public class NotFoundException extends ApiException {

    // Default-Fall
    public NotFoundException() {
        super(
                404,
                "PATH_NOT_FOUND",
                "Pfad ungültig"
        );
    }

    public NotFoundException(String errorCode, String message) {
        super(
                404,
                errorCode,
                message
        );
    }
}
