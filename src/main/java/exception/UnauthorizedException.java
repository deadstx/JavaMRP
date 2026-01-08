package exception;

public class UnauthorizedException extends ApiException {

  // Default-Fall
  public UnauthorizedException() {
    super(
            401,
            "AUTH_UNAUTHORIZED",
            "Nicht autorisiert"
    );
  }

  public UnauthorizedException(String errorCode, String message) {
    super(
            401,
            errorCode,
            message
    );
  }

  public static UnauthorizedException missingToken() {
    return new UnauthorizedException(
            "AUTH_MISSING_TOKEN",
            "Nicht autorisiert – kein Token vorhanden"
    );
  }

  public static UnauthorizedException invalidToken() {
    return new UnauthorizedException(
            "AUTH_INVALID_TOKEN",
            "Ungültiger oder abgelaufener Token"
    );
  }
}
