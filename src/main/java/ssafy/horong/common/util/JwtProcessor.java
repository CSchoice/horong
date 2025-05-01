package ssafy.horong.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ssafy.horong.common.exception.security.TokenExpiredException;
import ssafy.horong.common.exception.security.InvalidSignatureTokenException;
import ssafy.horong.common.exception.security.InvalidTokenException;
import ssafy.horong.common.exception.token.TokenTypeNotMatchedException;
import ssafy.horong.common.properties.JwtProperties;
import ssafy.horong.domain.auth.model.DecodedJwtToken;
import ssafy.horong.domain.auth.model.LoginToken;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.redis.BlacklistTokenRedisRepository;
import ssafy.horong.domain.redis.RefreshTokenRedisRepository;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Set;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.ACCESS_TOKEN;
import static ssafy.horong.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@Component
@Slf4j
public class JwtProcessor {
    
    public JwtProcessor(JwtProperties jwtProperties, 
                      BlacklistTokenRedisRepository blacklistTokenRedisRepository, 
                      RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.jwtProperties = jwtProperties;
        this.blacklistTokenRedisRepository = blacklistTokenRedisRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        initJwtParser();
    }
    
    private void initJwtParser() {
        this.jwtParser = Jwts.parser()
                .verifyWith((SecretKey) getSecretKey())
                .build();
        log.debug("JWT Parser initialized");
    }

    private final JwtProperties jwtProperties;
private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;
private final RefreshTokenRedisRepository refreshTokenRedisRepository;
private JwtParser jwtParser;

    public Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }

    public Jws<Claims> getClaim(String token) {
        log.debug("token : {}", token);
        if (isTokenExpired(token)) {
            throw new TokenExpiredException();
        }
        try {
            return jwtParser.parseSignedClaims(token);
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public void saveRefreshToken(String refreshToken, Long userId) {
        refreshTokenRedisRepository.save(refreshToken, userId.toString());
    }

    public void saveRefreshToken(LoginToken tokens, User user) {
        refreshTokenRedisRepository.save(tokens.refreshToken(), user.getId().toString());
    }

    public Long findUserIdByRefreshToken(String refreshToken) {
        String userId = refreshTokenRedisRepository.findById(refreshToken)
                .orElseThrow(InvalidTokenException::new);
        return Long.valueOf(userId);
    }

    public void renewRefreshToken(String oldRefreshToken, String newRefreshToken, User member) {
        refreshTokenRedisRepository.save(newRefreshToken, String.valueOf(member.getId()));
        expireToken(oldRefreshToken);
    }
    
    /**
     * 리프레시 토큰을 즉시 무효화합니다.
     * 토큰을 블랙리스트에 추가하고 저장소에서 제거합니다.
     * @param refreshToken 무효화할 리프레시 토큰
     */
    public void invalidateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            log.warn("무효화할 리프레시 토큰이 null입니다");
            return;
        }
        
        try {
            // 블랙리스트에 토큰 추가 (만료 시간까지)
            blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
            // Redis에서 토큰 제거
            refreshTokenRedisRepository.delete(refreshToken);
            log.info("리프레시 토큰 무효화 완료: {}", refreshToken);
        } catch (Exception e) {
            log.error("리프레시 토큰 무효화 실패: {}", e.getMessage());
            throw new InvalidTokenException("토큰 무효화 중 오류 발생");
        }
    }

    public void expireToken(String refreshToken) {
        if (refreshToken == null) {
            log.info("리프레시 토큰이 null이어서 토큰 만료 처리 건너뜀");
            return;
        }
        blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
        refreshTokenRedisRepository.delete(refreshToken);
        log.info("Token added to blacklist: {}", refreshToken);
    }
    
    /**
     * 사용자 ID로 모든 리프레시 토큰을 만료시킵니다.
     * @param userId 사용자 ID
     * @return 만료된 토큰 수
     */
    public int expireAllUserTokens(Long userId) {
        Set<String> userTokens = refreshTokenRedisRepository.findKeysByValue(userId.toString());
        int count = 0;
        
        for (String token : userTokens) {
            try {
                blacklistTokenRedisRepository.save(token, getRemainingTime(token));
                refreshTokenRedisRepository.delete(token);
                count++;
                log.info("Expired token for user {}: {}", userId, token);
            } catch (Exception e) {
                log.error("Error expiring token for user {}: {}", userId, e.getMessage());
            }
        }
        
        log.info("Expired {} tokens for user {}", count, userId);
        return count;
    }

    public long getRemainingTime(String token) {
        Claims claims = getClaim(token).getPayload();
        Date expiration = claims.getExpiration();
        Date now = new Date();
        return Math.max(0, expiration.getTime() - now.getTime());
    }

    public boolean isTokenExpired(String token) {
        Boolean blacklisted = blacklistTokenRedisRepository.hasKey(token);
        if (Boolean.TRUE.equals(blacklisted)) {
            return true;
        }
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return true;
        }
    }

    public String generateAccessToken(User user) {
        log.debug("access token exp : {}", jwtProperties.accessTokenExp());
        return issueToken(user.getId(), user.getRole(), ACCESS_TOKEN, jwtProperties.accessTokenExp());
    }

    public String generateRefreshToken(User member) {
        return issueToken(member.getId(), member.getRole(), REFRESH_TOKEN, jwtProperties.refreshTokenExp());
    }

    public DecodedJwtToken decodeToken(String token, String type) {
        Claims claims = getClaim(token).getPayload();
        checkType(claims, type);

        return new DecodedJwtToken(
                Long.valueOf(claims.getSubject()),
                String.valueOf(claims.get("role")),
                String.valueOf(claims.get("type"))
        );
    }

    private String issueToken(Long userId, MemberRole role, String type, Long time) {
        Date now = new Date();
        return Jwts.builder()
                .issuer("Cooing Inc.")
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + time))
                .claim("type", type)
                .claim("role", role.name())
                .signWith(getSecretKey())
                .compact();
    }

    private void checkType(Claims claims, String type) {
        if (!type.equals(String.valueOf(claims.get("type")))) {
            throw new TokenTypeNotMatchedException();
        }
    }

    

}
