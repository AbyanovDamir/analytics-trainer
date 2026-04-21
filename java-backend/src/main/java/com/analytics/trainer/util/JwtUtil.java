package com.analytics.trainer.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "your-secret-key-for-hackathon-2026";
    private static final long EXPIRATION_TIME = 86400000; // 24 часа
    
    public static String generateToken(int userId, String email) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }
    
    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token);
    }
    
    public static int getUserIdFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return Integer.parseInt(jwt.getSubject());
    }
}
