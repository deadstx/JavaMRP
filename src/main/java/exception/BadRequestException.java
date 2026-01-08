package exception;

public class BadRequestException extends ApiException {

    // Default-Fall
    public BadRequestException() {
        super(
                400,
                "EMPTY_BODY",
                "Request-Body darf nicht leer sein"
        );
    }

    public BadRequestException(String errorCode, String message) {
        super(
                400,
                errorCode,
                message
        );
    }
}
