package ssafy.horong.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.member.request.PasswordUpdateRequest;
import ssafy.horong.api.member.request.UserSignupRequest;
import ssafy.horong.api.member.request.UserUpdateRequest;
import ssafy.horong.api.member.response.*;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.common.util.UserUtil;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "회원관리")
public class MemberController {
    private final UserService userService;
    private final UserUtil userUtil;

    @Operation(summary = "회원가입", description = """
         소셜로그인 시 저장된 임시 회원 정보를 정식 회원으로 업데이트하는 API입니다.
         이 과정을 통해 해당 회원은 임시 회원이 아닌 정식 회원으로 전환됩니다.
     """)
    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public CommonResponse<UserSignupResponse> signup(@ModelAttribute @Validated UserSignupRequest request) {
        log.info("[UserController] 회원가입 >>>> request: {}", request);
        UserSignupResponse response = userService.signupMember(request.toCommand());
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하는 API입니다.")
    @PatchMapping
    public CommonResponse<?> deleteMember() {
        log.info("[UserController] 회원 탈퇴");
        String message = userService.deleteMember();
        return CommonResponse.ok(message, null);
    }

    @Operation(summary = "닉네임 중복 조회", description = "닉네임 중복 조회하는 API입니다.")
    @GetMapping("/nickname")
    public CommonResponse<String> checkNickname(@RequestParam String nickname) {
        log.info("[UserController] 닉네임 중복 조회 >>>> nickname: {}", nickname);
        boolean isDuplicated = userService.checkNickname(nickname);

        String message = isDuplicated ? "이미 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        return CommonResponse.ok(message, null);
    }

    @Operation(summary = "아이디 중복 조회", description = "아이디 중복 조회하는 API입니다.")
    @GetMapping("/userId")
    public CommonResponse<String> checkUserId(@RequestParam String userId) {
        log.info("[UserController] 아이디 중복 조회 >>>> userId: {}", userId);
        boolean isDuplicated = userService.checkUserId(userId);
        String message = isDuplicated ? "이미 사용중인 아이디입니다." : "사용 가능한 아이디입니다.";
        return CommonResponse.ok(message, null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "로그인 시 회원 정보 조회", description = "로그인 시 회원 정보를 조회하는 API입니다.")
    @GetMapping
    public CommonResponse<UserDetailResponse> getMemberDetail() {
        log.info("[UserController] 로그인 시 회원 정보 조회");
        UserDetailResponse response = userService.getMemberDetail();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "프로필에서 회원 정보 조회", description = "프로필에서 회원 정보를 조회하는 API입니다.")
    @GetMapping("/profile")
    public CommonResponse<UserProfileDetailResponse> getMemberProfileDetail() {
        log.info("[UserController] 프로필에서 회원 정보 조회");
        UserProfileDetailResponse response = userService.getMemberProfileDetail();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "회원 닉네임 수정", description = "회원 닉네임을 수정하는 API입니다.")
    @PutMapping(value = "/profile", consumes = { "multipart/form-data" })
    public CommonResponse<UserDetailResponse> updateMemberProfile(@ModelAttribute @Validated UserUpdateRequest request) {
        log.info("[UserController] 회원 정보 수정 >>>> request: {}", request);
        UserDetailResponse response = userService.updateMemberProfile(request.toCommand());
        return CommonResponse.ok(response);
    }

    @Operation(summary = "회원 비밀번호 수정", description = "회원 비밀번호를 수정하는 API입니다.")
    @PatchMapping(value = "/password")
    public CommonResponse<String> updateMemberPassword(@RequestBody PasswordUpdateRequest request) {
        log.info("[UserController] 회원 비밀번호 수정 >>>> request: {}", request);
        userService.updateMemberPassword(request.toCommand());
        return CommonResponse.ok("비밀번호가 성공적으로 변경되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "사용자의 ID 조회", description = "나의 id를 조회하는 API입니다.")
    @GetMapping("/id")
    public CommonResponse<UserIdResponse> getUserId() {
        log.info("[UserController] 플레이어의 ID 조회");
        UserIdResponse response = userService.getMemberId();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "사용자 언어 변경", description = "사용자 언어를 변경하는 API입니다.")
    @PatchMapping("/language")
    public CommonResponse<String> updateLanguage(@RequestParam Language language) {
        log.info("[UserController] 사용자 언어 변경 >>>> language: {}", language);
        userService.updateLanguage(language);
        return CommonResponse.ok("언어가 성공적으로 변경되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "프로필 잠금 여부 조회", description = "프로필 잠금 여부를 조회하는 API입니다.")
    @GetMapping("/profile/unlocked")
    public CommonResponse<List<ProfileUnlockedResponse> > getProfileUnlocked() {
        log.info("[UserController] 프로필 잠금 여부 조회");
        List<ProfileUnlockedResponse> isUnlocked = userService.getProfileUnlocked();
        return CommonResponse.ok(isUnlocked);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경하는 API입니다.")
    @PatchMapping("/profile/image")
    public CommonResponse<UserProfileDetailResponse> updateProfileImage(@RequestParam Integer profileImage) {
        log.info("[UserController] 프로필 이미지 변경 >>>> profileImage: {}", profileImage);
        UserProfileDetailResponse response = userService.updateProfileImage(profileImage);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "사용자 언어 조회", description = "사용자 언어를 조회하는 API입니다.")
    @GetMapping("/language")
    public CommonResponse<Language> getLanguage() {
        log.info("[UserController] 사용자 언어 조회");
        Language language = userUtil.getCurrentUser().getLanguage();
        return CommonResponse.ok(language);
    }
}
