package com.br.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger; // ⭐ ADICIONAR ESTE IMPORT
import org.slf4j.LoggerFactory; // ⭐ ADICIONAR ESTE IMPORT

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class); // ⭐ ADICIONAR LOG

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails, Long userId, String userType, String userName) {
        logger.debug("JwtUtil: Gerando token com userId={}, userType={}, userName={}", userId, userType, userName); // ⭐ LOG
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("userName", userName);
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(UserDetails userDetails) {
        logger.debug("JwtUtil: Gerando token (versão sem userId, userType, userName)"); // ⭐ LOG
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("JwtUtil: Criando token para subject: {}", subject); // ⭐ LOG
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
        logger.debug("JwtUtil: Obtendo chave de assinatura. Tamanho da chave: {} bytes", keyBytes.length); // ⭐ LOG
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.debug("JwtUtil: Validação do token para '{}': {}. Token expirado? {}", username, isValid, isTokenExpired(token)); // ⭐ LOG
        return isValid;
    }

    public String extractUsername(String token) {
        logger.debug("JwtUtil: Extraindo username (subject) do token..."); // ⭐ LOG
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        logger.debug("JwtUtil: Extraindo data de expiração do token..."); // ⭐ LOG
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        logger.debug("JwtUtil: Extraindo userId do token..."); // ⭐ LOG
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    public String extractUserType(String token) {
        logger.debug("JwtUtil: Extraindo userType do token..."); // ⭐ LOG
        Claims claims = extractAllClaims(token);
        return claims.get("userType", String.class);
    }

    public String extractEntityName(String token) {
        logger.debug("JwtUtil: Extraindo entityName ('userName') do token..."); // ⭐ LOG
        Claims claims = extractAllClaims(token);
        return claims.get("userName", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("JwtUtil: Extraindo todos os claims do token..."); // ⭐ LOG
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("JwtUtil: ERRO - Token JWT expirado: {}", e.getMessage()); // ⭐ LOG
            throw e;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("JwtUtil: ERRO - Token JWT malformado: {}", e.getMessage()); // ⭐ LOG
            throw e;
        } catch (io.jsonwebtoken.SignatureException e) {
            logger.error("JwtUtil: ERRO - Erro de assinatura do token JWT. Verifique a chave secreta no application.properties: {}", e.getMessage()); // ⭐ LOG
            throw e;
        } catch (Exception e) {
            logger.error("JwtUtil: ERRO - Erro inesperado ao parsear claims do token: {}", e.getMessage(), e); // ⭐ LOG
            throw new RuntimeException("Erro ao parsear claims do token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        boolean expired = expirationDate.before(new Date());
        logger.debug("JwtUtil: Verificando expiração. Data de Expiração: {}. Data Atual: {}. Expirado: {}", expirationDate, new Date(), expired); // ⭐ LOG
        return expired;
    }
}