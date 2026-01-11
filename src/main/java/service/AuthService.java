package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;
import io.github.cdimascio.dotenv.Dotenv;


import repository.UserRepository;

import java.util.Date;
import java.util.UUID;

public class AuthService {

    static Dotenv dotenv = Dotenv.load();
    private static final String SECRET = dotenv.get("JWT_SECRET");
    private static final Algorithm algorithm;

    static {
        assert SECRET != null;
        algorithm = Algorithm.HMAC256(SECRET);
    }

    private final UserRepository userRepository;
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean verifyUsername(String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }


    public boolean isValidLogin(String username, String password) {
        if(!verifyUsername(username)) return false;

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
        String username = jwt.getSubject();

        return userRepository.findIdByUsername(username);
    }


    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("mrp-api")
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1 Stunde
                .sign(algorithm);
    }


    public void verifyToken(String token) {
        JWT.require(algorithm)
                .withIssuer("mrp-api")
                .build()
                .verify(token);
    }

}
