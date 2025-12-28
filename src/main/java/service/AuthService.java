package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;

import repository.UserRepository;

import java.util.Date;
import java.util.UUID;

public class AuthService {

    private static final String SECRET = "super_secret_key_123_super_secret_key_super_secret_key";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private final UserRepository userRepository;

    // 🔑 Repository wird injected
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Login prüfen
    public boolean isValidLogin(String username, String password) {
        String storedHash = userRepository.findPasswordHashByUsername(username);

        if (storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(password, storedHash);
    }


    public UUID getUserIdFromToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("mrp-api")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        String username = jwt.getSubject(); // das gleiche wie Subject in generateToken

        // Falls User-ID in DB oder Token gespeichert ist:
        return userRepository.findIdByUsername(username);
    }



    // Token erstellen
    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("mrp-api")
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1 Stunde
                .sign(algorithm);
    }

    // Token verifizieren
    public void verifyToken(String token) {
        JWT.require(algorithm)
                .withIssuer("mrp-api")
                .build()
                .verify(token);
    }

}
