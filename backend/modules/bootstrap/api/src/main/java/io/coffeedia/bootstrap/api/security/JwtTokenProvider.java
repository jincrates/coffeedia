package io.coffeedia.bootstrap.api.security;

import io.coffeedia.domain.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성 및 검증을 담당하는 컴포넌트
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:coffeedia-jwt-secret-key-for-development-only-change-in-production}") String secret,
            @Value("${app.jwt.access-token-validity:3600000}") long accessTokenValidity,  // 1시간
            @Value("${app.jwt.refresh-token-validity:86400000}") long refreshTokenValidity // 24시간
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidity;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidity;
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(String username, List<String> roles) {
        return createToken(username, roles, accessTokenValidityInMilliseconds, "access");
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String username) {
        return createToken(username, List.of(), refreshTokenValidityInMilliseconds, "refresh");
    }

    /**
     * JWT 토큰 생성
     */
    private String createToken(String username, List<String> roles, long validityInMilliseconds, String tokenType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("tokenType", tokenType)
                .signWith(secretKey);

        // Access Token인 경우에만 역할 정보 추가
        if ("access".equals(tokenType) && roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 토큰에서 역할 정보 추출
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            
            if (expiration.before(new Date())) {
                log.debug("토큰이 만료되었습니다: {}", token);
                return false;
            }
            
            return true;
            
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String tokenType = (String) claims.get("tokenType");
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String tokenType = (String) claims.get("tokenType");
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 만료 시간 반환
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 토큰에서 Claims 추출
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다");
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("지원되지 않는 JWT 토큰입니다");
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException("잘못된 형식의 JWT 토큰입니다");
        } catch (SecurityException e) {
            throw new UnauthorizedException("JWT 서명이 유효하지 않습니다");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("JWT 토큰이 잘못되었습니다");
        }
    }
}
