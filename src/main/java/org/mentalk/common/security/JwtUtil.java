package org.mentalk.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.enums.Role;
import org.mentalk.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final int expMinutes = 1440;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long id, Role role) {
        try {
            Claims claims = Jwts.claims().setSubject(String.valueOf(id));
            claims.put("role", role.name());

            return Jwts.builder()
                       .setClaims(claims)
                       .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                       .setExpiration(Date.from(ZonedDateTime.now()
                                                             .plusMinutes(expMinutes)
                                                             .toInstant()))
                       .signWith(secretKey, SignatureAlgorithm.HS256)
                       .compact();
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.JWT_CREATION_ERROR);
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public Long getId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public Role getRole(String token) {
        return Role.valueOf(getClaims(token).get("role").toString());
    }
}
