package com.example.ERPSpringBootBackEnd.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ERPSpringBootBackEnd.model.Users;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {
    private String secret = "mySecret";
    public String getTokenFrom(String bearerToken) {
        final String bearer = "Bearer ";
        if(bearerToken == null || !bearerToken.startsWith(bearer)) {
            throw new JWTVerificationException("Invalid authorization token");
        }

        String token = bearerToken.substring(bearer.length());
        return token;
    }

    public String getSubjectFrom(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    public String generateToken(Users users) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        Instant expiration = generateExpirationTimeIn(10);
        String token = JWT.create()
                .withSubject(users.getUsername())
                .withExpiresAt(expiration)
                .withClaim("roles", users.getRole().name())
                .sign(algorithm);
        return token;
    }

    public Instant generateExpirationTimeIn(int min) {
        return LocalDateTime.now().plusMinutes(min).atZone(ZoneId.systemDefault()).toInstant();
    }


}
