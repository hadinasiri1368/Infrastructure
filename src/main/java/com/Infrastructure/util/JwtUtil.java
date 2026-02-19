package com.infrastructure.util;


import com.infrastructure.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil implements Serializable {

    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.expirationMinutes}")
    private int expiration;

    private static String secretKey;
    private static int expirationMinutes;
    private static final String CLAIMS_KEY = "userData";

    @PostConstruct
    public void init() {
        JwtUtil.secretKey = secret;
        JwtUtil.expirationMinutes = expiration;
    }

    public static String createToken(Object user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_KEY, user);
        return generateToken(claims);
    }

    public static Object getTokenData(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Claims claims = extractAllClaims(token);
            return claims.get(CLAIMS_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    private static String generateToken(Map<String, Object> claims) {
        if (secretKey == null || expirationMinutes == 0) {
            throw new IllegalStateException("JWT secret key or expiration minutes not initialized.");
        }
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(DateUtil.addMinutes(new Date(), expirationMinutes))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Claims extractAllClaims(String token) {
        if (secretKey == null) {
            throw new IllegalStateException("JWT secret key not initialized.");
        }
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static boolean validateToken(String token) {
        try {
            return !extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    private static <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    @FunctionalInterface
    private interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }

    public static long getRemainingValiditySeconds(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            long diffMillis = expirationDate.getTime() - System.currentTimeMillis();

            if (diffMillis <= 0) {
                return 0;
            }

            return diffMillis / 1000;
        } catch (Exception e) {
            return 0;
        }
    }

}
