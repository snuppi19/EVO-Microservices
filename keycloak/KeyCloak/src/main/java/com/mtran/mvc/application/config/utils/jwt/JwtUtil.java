package com.mtran.mvc.application.config.utils.jwt;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.config.utils.RSAKeyUtil;
import com.mtran.mvc.application.dto.request.LogoutRequest;
import com.mtran.mvc.application.dto.request.RefreshRequest;
import com.mtran.mvc.application.service.command.TokenCommandService;
import com.mtran.mvc.application.service.query.TokenQueryService;
import com.mtran.mvc.infrastructure.persistance.entity.InvalidateToken;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.InvalidatedTokenRepository;
import com.mtran.mvc.infrastructure.persistance.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final long EXPIRATION_TIME_TOKEN = 3600000L;//1 giờ
    private static final long EXPIRATION_TIME_REFRESH_TOKEN = 604800000L;// 7 ngày
    private final RSAKeyUtil rsaKeyUtil;
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final StringRedisTemplate redisTemplate;
    private final TokenQueryService tokenQueryService;


    // tao token
    public String generateToken(String username) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_TOKEN))
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    //tạo refresh token
    public String generateRefreshToken(String username) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH_TOKEN))
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    //  refresh access token ( email duoc dung lam usernam)
    public String refreshToken(RefreshRequest RefreshRequest) throws Exception {
        String email = RefreshRequest.getEmail();
        String accessToken= tokenQueryService.getAccessToken(email);
        var signJWT = validateToken(accessToken);
        String jit = signJWT.getId();
        Date expiryTime = signJWT.getExpiration();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidateToken);
        return generateToken(email);
    }

    // kiem tra token
    public Claims validateToken(String token) throws Exception {

        PublicKey publicKey = rsaKeyUtil.getPublicKey();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (invalidatedTokenRepository.existsById(claims.getId())) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey("invalid_token:" + claims.getId()))) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        String email = claims.getSubject();
        UserEntity userEntity = userRepository.findByEmail(email);
        //check issueAt so với lastchangePassword để xác thực được token còn hiệu lực hay không
        LocalDateTime lastChangePassword = userEntity.getLastChangePassword();
        if (lastChangePassword != null) {
            Date issuedAt = claims.getIssuedAt();
            LocalDateTime issuedAtTime = issuedAt.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (issuedAtTime.isBefore(lastChangePassword)) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
        }
        return claims;
    }

    //vo hieu hoa ca access token va refresh token
    public void logout(LogoutRequest request) throws Exception {
        //access token
        if (request.getToken() != null) {
            var signToken = validateToken(request.getToken());

            String jit = signToken.getId();
            Date expiryTime = signToken.getExpiration();
            /* CÁCH LƯU BLACKLIST DATABASE
            InvalidateToken invalidateToken = InvalidateToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidateToken);
             */
            long TTL = (expiryTime.getTime() - System.currentTimeMillis()) / 1000;
            redisTemplate.opsForValue().set("invalid_token:" + jit, jit, TTL, TimeUnit.SECONDS);
        }
        //refresh token
        if (request.getRefreshToken() != null) {
            var refreshSignToken = validateToken(request.getRefreshToken());
            String refreshJit = refreshSignToken.getId();
            Date refreshExpiryTime = refreshSignToken.getExpiration();
            /*  CÁCH LƯU BLACKLIST DATABASE
            InvalidateToken refreshInvalidateToken = InvalidateToken.builder()
                    .id(refreshJit)
                    .expiryTime(refreshExpiryTime)
                    .build();
            invalidatedTokenRepository.save(refreshInvalidateToken);
             */
            long TTL = (refreshExpiryTime.getTime() - System.currentTimeMillis()) / 1000;
            redisTemplate.opsForValue().set("invalid_token:" + refreshJit, refreshJit, TTL, TimeUnit.SECONDS);
        }
    }

    //lay ra email(unique) cua nguoi dung tu token
    public String extractEmail(String token) throws Exception {
        PublicKey publicKey = rsaKeyUtil.getPublicKey();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}

