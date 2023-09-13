package com.simpleregisterlogin.utils;

import com.simpleregisterlogin.exceptions.InvalidTokenException;
import com.simpleregisterlogin.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final Environment env;

    public JwtUtil(Environment env) {
        this.env = env;
    }

    public String extractTokenFromHeaderAuthorization(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractId(String authorizationHeader) {
        Claims claims = extractAllClaims(extractTokenFromHeaderAuthorization(authorizationHeader));
        return Long.parseLong(claims.get("UserId").toString());
    }

    public boolean extractAdmin(String authorizationHeader) {
        Claims claims = extractAllClaims(extractTokenFromHeaderAuthorization(authorizationHeader));
        return Boolean.getBoolean(claims.get("isAdmin").toString());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(env.getProperty("SECRET_KEY")).parseClaimsJws(token).getBody();
        } catch (JwtException exception) {
            throw new InvalidTokenException(token);
        }
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("UserId", userDetails.getId());
        claims.put("isAdmin", userDetails.isAdmin());
        return createToken(claims, userDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, env.getProperty("SECRET_KEY"))
                .compact();
    }

    public Boolean validateToken(String token, UserDetailsImpl userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
