package org.example.logisticapplication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final String secret;
    private final long expirationTime;

    @Autowired
    public JwtTokenProvider(
            @Value("${jwt.secret}")
            String secret,
            @Value("${jwt.lifetime}")
            long expirationTime
    ) {
        this.secret = secret;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        log.info("USER INFO FOR GENERATING TOKEN! {}", user);

        var roleList = user.getRoleEntities()
                .stream().toList();
        claims.put("roles", roleList);

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.info("Parsed claims {}", claims);

        var username = claims.getSubject();
        var authorities = extractRoles(claims);
        log.info("USERNAME IS {}", username);
        log.info("ROLES IS {}", authorities);

        var userDetails = new User(username, "", authorities);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
        log.info("AUTHENTICATED IS {}", authentication);
        return authentication;
    }

    private List<SimpleGrantedAuthority> extractRoles(Claims claims) {
        List<Map<String, Object>> roles = claims.get("roles", List.class);

        return roles.stream()
                .map(roleMap -> new SimpleGrantedAuthority("ROLE_" + roleMap.get("name").toString()))
                .toList();
    }
}
