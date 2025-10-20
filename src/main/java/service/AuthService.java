package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class AuthService {

    // secret key für Signatur
    private static final String SECRET = "super_secret_key_123";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    // Token erzeugen
    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("mrp-api")
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1h gültig
                .sign(algorithm);
    }

    // Token validieren
    public String verifyToken(String token) {
        return JWT.require(algorithm)
                .withIssuer("mrp-api")
                .build()
                .verify(token)
                .getSubject(); // Gibt den Usernamen zurück
    }
}
