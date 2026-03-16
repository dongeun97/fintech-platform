package com.fintech.fintech_platform.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 토큰 서명에 사용할 비밀키
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 유효시간 (1시간)
    private final long expireTime = 1000 * 60 * 60;

    // 토큰 생성
    public String createToken(String username, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        var builder = Jwts.builder();
        builder.setSubject(username);
        builder.claim("role", role);
        builder.setIssuedAt(now);
        builder.setExpiration(expireDate);
        builder.signWith(key);

        return builder.compact();
    }

    // 토큰에서 username 꺼내기
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 role 꺼내기
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 파싱
    private Claims getClaims(String token) {
        var parserBuilder = Jwts.parserBuilder();
        parserBuilder.setSigningKey(key);

        return parserBuilder.build().parseClaimsJws(token).getBody();
    }
}