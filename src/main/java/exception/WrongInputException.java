package exception;

public class WrongInputException extends ApiException {

    // Default-Fall
    public WrongInputException() {
        super(
                400,
                "WRONG_INPUT",
                "Die Eingabe stimmt nicht mit der Vorgabe überein"
        );
    }

    public WrongInputException(String errorCode, String message) {
        super(
                400,
                errorCode,
                message
        );
    }
}
