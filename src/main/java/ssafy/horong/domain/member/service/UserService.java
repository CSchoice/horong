package ssafy.horong.domain.member.service;

import ssafy.horong.api.member.response.*;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;
import ssafy.horong.domain.member.common.Language;

import java.util.List;

public interface UserService {

    // 회원가입
    UserSignupResponse signupMember(MemberSignupCommand signupCommand);

    // 회원 정보 조회
    UserDetailResponse getMemberDetail();
    UserProfileDetailResponse getMemberProfileDetail();
    UserIdResponse getMemberId();

    // 회원 정보 수정
    UserDetailResponse updateMemberProfile(UpdateProfileCommand command);
    UserProfileDetailResponse updateProfileImage(Integer profileImageNumber);
    void updateLanguage(Language language);
    void updateMemberPassword(PasswordUpdateCommand command);

    // 회원 탈퇴
    String deleteMember();

    // 중복 체크
    boolean checkNickname(String nickname);
    boolean checkUserId(String userId);

    // 프로필 잠금 해제 목록 조회
    List<ProfileUnlockedResponse> getProfileUnlocked();
}
