package ssafy.horong.domain.member.service.sensitive;

import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.entity.UserSensitiveInfo;

import java.util.Optional;

/**
 * 사용자 민감 정보 처리 서비스 인터페이스
 */
public interface UserSensitiveInfoService {

    /**
     * 사용자의 민감 정보를 저장합니다
     *
     * @param user 사용자
     * @param phoneNumber 전화번호
     * @param email 이메일
     * @param address 주소
     * @param detailAddress 상세 주소
     * @param birthDate 생년월일
     * @return 저장된 민감 정보 엔티티
     */
    UserSensitiveInfo saveSensitiveInfo(User user, String phoneNumber, String email, String address, String detailAddress, String birthDate);

    /**
     * 사용자의 민감 정보를 조회합니다
     *
     * @param user 사용자
     * @return 민감 정보 엔티티 (없을 경우 빈 Optional)
     */
    Optional<UserSensitiveInfo> getSensitiveInfo(User user);

    /**
     * 사용자의 민감 정보를 업데이트합니다
     *
     * @param user 사용자
     * @param phoneNumber 전화번호
     * @param email 이메일
     * @param address 주소
     * @param detailAddress 상세 주소
     * @param birthDate 생년월일
     * @return 업데이트된 민감 정보 엔티티
     */
    UserSensitiveInfo updateSensitiveInfo(User user, String phoneNumber, String email, String address, String detailAddress, String birthDate);

    /**
     * 전화번호로 사용자를 검색합니다 (메모리에서 복호화하여 검색)
     *
     * @param phoneNumber 검색할 전화번호 (부분 일치)
     * @return 전화번호가 일치하는 사용자 민감 정보 목록
     */
    Iterable<UserSensitiveInfo> findByPhoneNumber(String phoneNumber);

    /**
     * 이메일로 사용자를 검색합니다 (메모리에서 복호화하여 검색)
     *
     * @param email 검색할 이메일 (부분 일치)
     * @return 이메일이 일치하는 사용자 민감 정보 목록
     */
    Iterable<UserSensitiveInfo> findByEmail(String email);

    /**
     * 주소로 사용자를 검색합니다 (메모리에서 복호화하여 검색)
     *
     * @param address 검색할 주소 (부분 일치)
     * @return 주소가 일치하는 사용자 민감 정보 목록
     */
    Iterable<UserSensitiveInfo> findByAddress(String address);
}
