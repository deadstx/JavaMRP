package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class AuthService {

    // secret key für JWT
    private static final String SECRET = "super_secret_key_123"; // eigentlich in .env
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private final ObjectMapper mapper = new ObjectMapper();


    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("mrp-api") // wer hat erstellt
                .withSubject(username) // speichert user im token
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1h
                .sign(algorithm); // signiert mit secret key
    }


    public void verifyToken(String token) {
        JWT.require(algorithm) // erstellt einen builder mit ausgewähltem algo
                .withIssuer("mrp-api") // von wem kommen muss
                .build() // baut den verifier
                .verify(token) // prüft token
                .getSubject();
    }

    public boolean isValidLogin(String username, String password) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sampleUsers.json")) {
            if (is == null) {
                throw new AuthException("Benutzerdaten-Datei 'sampleUsers.json' wurde nicht gefunden");
            }

            List<User> users = mapper.readValue(is, new TypeReference<>() {});
            return users.stream().anyMatch(user ->
                    user.getUsername().equals(username) &&
                            user.getPassword().equals(password)
            );

        } catch (IOException e) {
            throw new AuthException("Fehler beim Lesen der Benutzerdatei", e);
        } catch (Exception e) {
            throw new AuthException("Allgemeiner Authentifizierungsfehler", e);
        }
    }


}
