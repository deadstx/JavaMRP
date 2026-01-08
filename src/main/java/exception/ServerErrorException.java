package exception;

public class ServerErrorException extends ApiException {

    // Default-Fall
    public ServerErrorException() {
        super(
                500,
                "INTERNAL_SERVER_ERROR",
                "Ein unerwarteter Fehler ist aufgetreten"
        );
    }

    public ServerErrorException(String errorCode, String message) {
        super(
                500,
                errorCode,
                message
        );
    }
}
