package com.example.todo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.todo.entity.Users;
import com.example.todo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Component
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    @Value("${JWT_secret}")
    private String secret;

    private final UserRepository userRepository;

    public JWTUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        logger.info("Generating token for username: {}", username);
        String token = JWT.create()
                .withSubject("User Details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //1hr
                .sign(Algorithm.HMAC256(secret));
        logger.info("Token generated successfully: {}", token);
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<Users> getUsernameFromToken(String token) {
        try {
            logger.info("Verifying token: {}", token);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getClaim("username").asString();
            logger.info("Username from token: {}", username);
            
            if (username == null || username.isEmpty()) {
                logger.error("Username is null or empty in token");
                return Optional.empty();
            }
            
            Optional<Users> user = userRepository.findByUsername(username);
            logger.info("User found in database: {}", user.isPresent());
            return user;
        } catch (Exception e) {
            logger.error("Error verifying token: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(String token, String username) {
        try {
            logger.info("Validating token: {} for username: {}", token, username);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            
            // Check if token is expired
            Date expiresAt = jwt.getExpiresAt();
            Date now = new Date();
            logger.info("Token expires at: {}, current time: {}", expiresAt, now);
            
            if (expiresAt.before(now)) {
                logger.error("Token has expired");
                return false;
            }
            
            // Check if username matches
            String tokenUsername = jwt.getClaim("username").asString();
            logger.info("Token username: {}, provided username: {}", tokenUsername, username);
            
            boolean isValid = tokenUsername != null && tokenUsername.equals(username);
            logger.info("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage(), e);
            return false;
        }
    }
}
