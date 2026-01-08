package exception;

public class InvalidJsonException extends ApiException {

    // Default-Fall
    public InvalidJsonException() {
        super(
                400,
                "INVALID_JSON",
                "Ungültiges JSON-Format"
        );
    }

    public InvalidJsonException(String errorCode, String message) {
        super(
                400,
                errorCode,
                message
        );
    }
}
