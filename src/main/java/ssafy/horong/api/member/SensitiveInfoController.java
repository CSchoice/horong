package ssafy.horong.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.member.dto.SensitiveInfoDto;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.entity.UserSensitiveInfo;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.domain.member.service.sensitive.UserSensitiveInfoService;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 사용자 민감 정보 API 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/sensitive")
@Tag(name = "민감 정보 API", description = "사용자 민감 정보 관리 API")
public class SensitiveInfoController {

    private final UserSensitiveInfoService sensitiveInfoService;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 민감 정보 조회
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "민감 정보 조회", description = "현재 로그인한 사용자의 민감 정보를 조회합니다.")
    public ResponseEntity<SensitiveInfoDto> getSensitiveInfo() {
        return SecurityUtil.getLoginMemberId()
                .flatMap(userId -> userRepository.findById(userId))
                .flatMap(user -> sensitiveInfoService.getSensitiveInfo(user))
                .map(SensitiveInfoDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 현재 로그인한 사용자의 민감 정보 저장/수정
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "민감 정보 저장/수정", description = "현재 로그인한 사용자의 민감 정보를 저장하거나 수정합니다.")
    public ResponseEntity<SensitiveInfoDto> saveSensitiveInfo(@Valid @RequestBody SensitiveInfoDto dto) {
        Optional<User> currentUser = SecurityUtil.getLoginMemberId()
                .flatMap(userId -> userRepository.findById(userId));

        if (currentUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserSensitiveInfo savedInfo = sensitiveInfoService.saveSensitiveInfo(
                currentUser.get(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                dto.getAddress(),
                dto.getDetailAddress(),
                dto.getBirthDate()
        );

        return ResponseEntity.ok(SensitiveInfoDto.from(savedInfo));
    }

    /**
     * 관리자: 이메일로 사용자 검색 (부분 일치)
     */
    @GetMapping("/search/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "이메일로 사용자 검색 (관리자 전용)", description = "이메일로 사용자를 검색합니다. 부분 일치를 지원합니다.")
    public ResponseEntity<Iterable<SensitiveInfoDto>> searchByEmail(@PathVariable String email) {
        Iterable<UserSensitiveInfo> results = sensitiveInfoService.findByEmail(email);
        
        return ResponseEntity.ok(
                StreamSupport.stream(results.spliterator(), false)
                        .map(SensitiveInfoDto::from)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 관리자: 전화번호로 사용자 검색 (부분 일치)
     */
    @GetMapping("/search/phone/{phoneNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전화번호로 사용자 검색 (관리자 전용)", description = "전화번호로 사용자를 검색합니다. 부분 일치를 지원합니다.")
    public ResponseEntity<Iterable<SensitiveInfoDto>> searchByPhoneNumber(@PathVariable String phoneNumber) {
        Iterable<UserSensitiveInfo> results = sensitiveInfoService.findByPhoneNumber(phoneNumber);
        
        return ResponseEntity.ok(
                StreamSupport.stream(results.spliterator(), false)
                        .map(SensitiveInfoDto::from)
                        .collect(Collectors.toList())
        );
    }
}
