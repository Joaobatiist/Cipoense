package com.br.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class jwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(jwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails, UUID userId, String userType, String userName) {
        logger.debug("JwtUtil: Gerando token com userId={}, userType={}, userName={}", userId, userType, userName);
        Map<String, Object> claims = new HashMap<>();
        // Armazena o UUID como String no token
        claims.put("userId", userId.toString());
        claims.put("userType", userType);
        claims.put("userName", userName);
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(UserDetails userDetails) {
        logger.debug("JwtUtil: Gerando token (versão sem userId, userType, userName)");
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("JwtUtil: Criando token para subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.debug("JwtUtil: Validação do token para '{}': {}. Token expirado? {}", username, isValid, isTokenExpired(token));
        return isValid;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public UUID extractUserId(String token) {
        logger.debug("JwtUtil: Extraindo userId do token...");
        Claims claims = extractAllClaims(token);


        String userIdAsString = claims.get("userId", String.class);


        if (userIdAsString != null) {
            try {
                return UUID.fromString(userIdAsString);
            } catch (IllegalArgumentException e) {
                logger.error("JwtUtil: ERRO - O 'userId' no token não é um UUID válido: {}", userIdAsString, e);
                return null;
            }
        }

        return null;
    }


    public String extractUserType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userType", String.class);
    }

    public String extractEntityName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userName", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("JwtUtil: ERRO ao parsear claims do token: {}", e.getMessage());
            // Lançar uma exceção mais específica pode ser útil
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}