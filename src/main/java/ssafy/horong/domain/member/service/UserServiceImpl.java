package ssafy.horong.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.member.response.*;
import ssafy.horong.common.constant.global.S3Image;
import ssafy.horong.common.exception.User.*;
import ssafy.horong.common.exception.security.InvalidPasswordException;
import ssafy.horong.common.exception.security.NotAuthenticatedException;
import ssafy.horong.common.exception.security.PasswordUsedException;
import ssafy.horong.common.exception.token.TokenSaveFailedException;
import ssafy.horong.common.util.JwtProcessor;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.education.entity.EducationDay;
import ssafy.horong.domain.education.repository.EducationDayRepository;
import ssafy.horong.domain.education.repository.EducationStampRepository;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.PasswordHistory;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.PasswordHistoryRepository;
import ssafy.horong.domain.member.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final RedisTemplate<String, String> redisTemplateLang;
    private final EducationDayRepository educationDayRepository;
    private final EducationStampRepository educationStampRepository;

    private static final String FORBIDDEN_WORDS_KEY = "forbiddenWords";
    private static final int MAX_PROFILE_IMG = 16;

    @Override
    @Transactional
    public UserSignupResponse signupMember(MemberSignupCommand cmd) {

        validateSignupCommand(cmd);

        if (isDuplicateUserId(cmd.userId())) throw new UserIdDuplicateException();

        User user = User.builder()
                .nickname(cmd.nickname())
                .build();

        String encodedPwd = passwordEncoder.encode(cmd.password());
        user.signupMember(cmd, encodedPwd, cmd.language());
        user.setProfileImg(S3Image.DEFAULT_URL);

        userRepository.save(user);

        passwordHistoryRepository.save(
                PasswordHistory.builder().user(user).password(encodedPwd).build()
        );

        educationDayRepository.save(
                EducationDay.builder().user(user).wordIds(new ArrayList<>()).day(1).build()
        );

        try {
            String accessToken = jwtProcessor.generateAccessToken(user);
            String refreshToken = jwtProcessor.generateRefreshToken(user);
            jwtProcessor.saveRefreshToken(refreshToken, user.getId());
            return UserSignupResponse.of(accessToken, refreshToken);
        } catch (Exception e) {
            throw new TokenSaveFailedException();
        }
    }

    @Override
    public UserDetailResponse getMemberDetail() {
        User u = getCurrentUser();
        return UserDetailResponse.of(presign(u.getProfileImg()), u.getNickname());
    }

    @Override
    public UserProfileDetailResponse getMemberProfileDetail() {
        User u = getCurrentUser();
        return UserProfileDetailResponse.of(presign(u.getProfileImg()), u.getNickname());
    }

    @Override
    public UserIdResponse getMemberId() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        return UserIdResponse.of(userId);
    }

    @Override
    @Transactional
    public UserDetailResponse updateMemberProfile(UpdateProfileCommand cmd) {
        validateUpdateProfileCommand(cmd);
        User u = getCurrentUser();
        u.updateProfile(optionalOrDefault(cmd.nickname(), u.getNickname()));
        userRepository.save(u);
        return UserDetailResponse.of(presign(u.getProfileImg()), u.getNickname());
    }

    @Transactional
    public UserProfileDetailResponse updateProfileImage(Integer num) {
        User u = getCurrentUser();
        u.setProfileImg(num.toString());
        userRepository.save(u);
        return UserProfileDetailResponse.of(presign(u.getProfileImg()), u.getNickname());
    }

    @Override
    @Transactional
    public String deleteMember() {
        User u = getCurrentUser();
        u.delete();
        userRepository.save(u);
        return "회원 탈퇴가 성공적으로 처리되었습니다.";
    }

    @Override
    @Transactional
    public void updateMemberPassword(PasswordUpdateCommand cmd) {

        User u = getCurrentUser();
        verifyCurrentPassword(cmd.currentPassword(), u);
        verifyNewPassword(cmd.newPassword(), u);

        String enc = passwordEncoder.encode(cmd.newPassword());
        u.updatePassword(enc);

        passwordHistoryRepository.save(
                PasswordHistory.builder().user(u).password(enc).build()
        );
    }

    @Override
    public boolean checkNickname(String nickname) {
        if (isDuplicateNickname(nickname)) throw new NickNameDuplicateException();
        return true;
    }

    @Override
    public boolean checkUserId(String userId) {
        if (isDuplicateUserId(userId)) throw new UserIdDuplicateException();
        return true;
    }

    @Transactional
    public void updateLanguage(Language language) {
        User u = getCurrentUser();
        u.setLanguage(language);
        userRepository.save(u);
    }

    public List<ProfileUnlockedResponse> getProfileUnlocked() {
        User u = getCurrentUser();
        int unlocked = educationStampRepository.countByUser(u) / 5 + 5;
        List<ProfileUnlockedResponse> list = new ArrayList<>();
        for (int i = 1; i <= MAX_PROFILE_IMG; i++) {
            list.add(new ProfileUnlockedResponse(
                    i,
                    i <= unlocked,
                    String.format("https://horong-service.s3.ap-northeast-2.amazonaws.com/profileImg/%d.png", i)
            ));
        }
        return list;
    }

    private User getCurrentUser() {
        Long id = SecurityUtil.getLoginMemberId().orElseThrow(NotAuthenticatedException::new);
        User u = userRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        if (u.isDeleted()) throw new NotAuthenticatedException();
        return u;
    }

    private String presign(String img) {
        return (img == null || img.isBlank()) ? "" : s3Util.getProfilePresignedUrlFromS3(img);
    }

    private String optionalOrDefault(String newVal, String currVal) {
        return (newVal == null || newVal.isBlank()) ? currVal : newVal;
    }

    private boolean isDuplicateUserId(String userId) {
        return userRepository.findByUserId(userId).filter(u -> !u.isDeleted()).isPresent();
    }

    private boolean isDuplicateNickname(String nickname) {
        return userRepository.findByNickname(nickname).filter(u -> !u.isDeleted()).isPresent();
    }

    private void verifyCurrentPassword(String raw, User u) {
        if (!passwordEncoder.matches(raw, u.getPassword())) throw new PasswordNotMatchException();
    }

    private void verifyNewPassword(String pwd, User u) {
        if (pwd.length() < 8 || pwd.length() > 20 || !pwd.matches(".*[!@#$%^&*].*"))
            throw new InvalidPasswordException();

        for (PasswordHistory h : passwordHistoryRepository.getHistoriesByUserId(u.getId())) {
            boolean same = passwordEncoder.matches(pwd, h.getPassword());
            boolean recent = h.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(6));
            if (same && recent) throw new PasswordUsedException();
        }
    }

    public void validateSignupCommand(MemberSignupCommand c) {

        Set<String> forbidden = redisTemplateLang.opsForSet().members(FORBIDDEN_WORDS_KEY);

        if (c.userId().length() < 2 || c.userId().length() > 16) throw new UserIdNotValidException();
        if (!c.userId().matches("^[a-zA-Z0-9]+$"))          throw new NotAllowedUseridException();
        if (containsForbiddenWord(c.userId(), forbidden))   throw new ForbiddenWordContainedException();

        if (c.password().length() < 8 || c.password().length() > 20) throw new PasswordNotValidException();
        if (!c.password().matches(".*[!@#$%^&*].*"))                   throw new InvalidPasswordException();

        if (c.nickname().length() < 2 || c.nickname().length() > 20) throw new NicknameNotValidException();
        if (!c.nickname().matches("^[a-zA-Z0-9가-힣\\u4E00-\\u9FFF]+$"))
            throw new NotAllowedNicknameException();
        if (containsForbiddenWord(c.nickname(), forbidden)) throw new ForbiddenWordContainedException();

        if (!Arrays.asList(Language.values()).contains(c.language())) throw new LanguageNotValidException();
        if (isDuplicateUserId(c.userId()))   throw new UserIdDuplicateException();
        if (isDuplicateNickname(c.nickname())) throw new NickNameDuplicateException();
    }

    public void validateUpdateProfileCommand(UpdateProfileCommand c) {
        Set<String> forbidden = redisTemplateLang.opsForSet().members(FORBIDDEN_WORDS_KEY);
        if (c.nickname() == null) return;

        if (c.nickname().length() < 2 || c.nickname().length() > 20) throw new NicknameNotValidException();
        if (isDuplicateNickname(c.nickname())) throw new NickNameDuplicateException();
        if (!c.nickname().matches("^[a-zA-Z0-9가-힣\\u4E00-\\u9FFF]+$"))
            throw new NotAllowedNicknameException();
        if (containsForbiddenWord(c.nickname(), forbidden)) throw new ForbiddenWordContainedException();
    }

    private boolean containsForbiddenWord(String text, Set<String> forbidden) {
        if (forbidden == null || forbidden.isEmpty()) return false;
        for (String w : forbidden) if (text.contains(w)) return true;
        return false;
    }
}
