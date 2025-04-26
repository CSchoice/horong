package ssafy.horong.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.auth.request.TokenRefreshRequest;
import ssafy.horong.api.auth.response.AuthResponse;
import ssafy.horong.common.exception.User.PasswordNotMatchException;
import ssafy.horong.common.exception.token.TokenSaveFailedException;
import ssafy.horong.common.util.JwtParser;
import ssafy.horong.common.util.JwtProcessor;
import ssafy.horong.domain.auth.command.LoginCommand;
import ssafy.horong.domain.auth.model.DecodedJwtToken;
import ssafy.horong.domain.auth.model.LoginToken;
import ssafy.horong.domain.member.common.CustomUserDetails;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.common.exception.User.*;

import ssafy.horong.common.exception.security.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.ACCESS_TOKEN;
import static ssafy.horong.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public AuthResponse login(LoginCommand command) {

        LoginToken tokens;
        String userId = command.userId();
        String password = command.password();

        User user = findMemberByUserId(userId)
                .orElseThrow(EmailNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException();
        }

        Authentication newAuthentication = SecurityContextHolder.getContext().getAuthentication(); // 기본값으로 초기화


        if (user.getRole() == MemberRole.ADMIN) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 기존 권한을 가져옴
            Collection<GrantedAuthority> currentAuthorities = new ArrayList<>(authentication.getAuthorities());

            // 새로운 권한 추가
            currentAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // 새로운 Authentication 객체 생성 (기존 인증 정보 사용)
            newAuthentication = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    currentAuthorities
            );

            // 새로운 Authentication 객체를 SecurityContext에 설정
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }

        log.info("권한 확인 {}", newAuthentication);


        tokens = generateTokens(user);
        jwtProcessor.saveRefreshToken(tokens, user);

        return AuthResponse.of(tokens.accessToken(), tokens.refreshToken());
    }

    @Override
    @Transactional
    public void logout() {
        log.info("[AuthService] 로그아웃");
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            // 토큰에서 사용자 ID를 추출
            DecodedJwtToken decodedJwtToken = jwtProcessor.decodeToken(token, ACCESS_TOKEN);
            Long userId = decodedJwtToken.memberId();
            
            // 사용자의 모든 리프레시 토큰 만료 처리
            int expiredTokenCount = jwtProcessor.expireAllUserTokens(userId);
            log.info("사용자 ID: {}의 로그아웃 처리되었습니다. 만료된 토큰 수: {}", userId, expiredTokenCount);
        } else {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }
    }

    @Override
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        log.info("[AuthService] 토큰 갱신 시작 >>>> Refresh 토큰: {}", request.refreshToken());
        
        // 1. 리프레시 토큰으로 사용자 ID 조회 및 검증
        Long userId = jwtProcessor.findUserIdByRefreshToken(request.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);
        
        // 2. 리프레시 토큰 유효성 검증
        DecodedJwtToken decodedJwtToken = jwtProcessor.decodeToken(request.refreshToken(), REFRESH_TOKEN);
        if (!decodedJwtToken.memberId().equals(userId)) {
            throw new InvalidTokenException();
        }
        
        try {
            // 3. 기존 리프레시 토큰 즉시 블랙리스트 처리 (1회용 적용)
            jwtProcessor.invalidateRefreshToken(request.refreshToken());
            
            // 4. 새 액세스 토큰과 리프레시 토큰 발급
            String newAccessToken = jwtProcessor.generateAccessToken(user);
            String newRefreshToken = jwtProcessor.generateRefreshToken(user);
            
            // 5. 새 리프레시 토큰 저장
            jwtProcessor.saveRefreshToken(newRefreshToken, user.getId());
            
            log.info("[AuthService] 토큰 갱신 완료 - 새로운 리프레시 토큰 발급됨");
            return AuthResponse.of(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            log.error("[AuthService] 토큰 갱신 중 오류 발생", e);
            throw new TokenSaveFailedException();
        }
    }

    private Optional<User> findMemberByUserId(String userId) {
        return userRepository.findNotDeletedUserByUserId(userId);
    }

    private LoginToken generateTokens(User member) {
        String accessToken = jwtProcessor.generateAccessToken(member);

        String refreshToken = jwtProcessor.generateRefreshToken(member);
        return new LoginToken(accessToken, refreshToken);
    }

    private Optional<User> findMemberById(DecodedJwtToken decodedJwtToken) {
        Long id = decodedJwtToken.memberId();
        return userRepository.findById(id);
    }
}