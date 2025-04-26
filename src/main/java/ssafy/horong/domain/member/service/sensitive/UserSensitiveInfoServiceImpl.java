package ssafy.horong.domain.member.service.sensitive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.common.crypto.CryptoSearchHelper;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.entity.UserSensitiveInfo;
import ssafy.horong.domain.member.repository.UserSensitiveInfoRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 민감 정보 처리 서비스 구현
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class UserSensitiveInfoServiceImpl implements UserSensitiveInfoService {

    private final UserSensitiveInfoRepository userSensitiveInfoRepository;
    private final CryptoSearchHelper cryptoSearchHelper;
    private final UserSensitiveInfoService self; // ✅ 자기 자신 인터페이스 타입으로 주입

    @Override
    @Transactional
    public UserSensitiveInfo saveSensitiveInfo(User user, String phoneNumber, String email, String address, String detailAddress, String birthDate) {
        Optional<UserSensitiveInfo> existingInfo = userSensitiveInfoRepository.findByUser(user);
        if (existingInfo.isPresent()) {
            // ✅ 자기 자신을 통해 호출
            return self.updateSensitiveInfo(user, phoneNumber, email, address, detailAddress, birthDate);
        }

        UserSensitiveInfo sensitiveInfo = UserSensitiveInfo.builder()
                .user(user)
                .phoneNumber(phoneNumber)
                .email(email)
                .address(address)
                .detailAddress(detailAddress)
                .birthDate(birthDate)
                .build();

        return userSensitiveInfoRepository.save(sensitiveInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSensitiveInfo> getSensitiveInfo(User user) {
        return userSensitiveInfoRepository.findByUser(user);
    }

    @Override
    @Transactional
    public UserSensitiveInfo updateSensitiveInfo(User user, String phoneNumber, String email, String address, String detailAddress, String birthDate) {
        UserSensitiveInfo sensitiveInfo = userSensitiveInfoRepository.findByUser(user)
                .orElseGet(() -> UserSensitiveInfo.builder().user(user).build());
                
        sensitiveInfo.updateInfo(phoneNumber, email, address, detailAddress, birthDate);
        return userSensitiveInfoRepository.save(sensitiveInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserSensitiveInfo> findByPhoneNumber(String phoneNumber) {
        // 모든 민감 정보를 가져와서 메모리에서 복호화하여 검색
        List<UserSensitiveInfo> allSensitiveInfos = userSensitiveInfoRepository.findAll();
        
        return cryptoSearchHelper.filterByEncryptedField(
                allSensitiveInfos,
                UserSensitiveInfo::getPhoneNumber,
                phoneNumber
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserSensitiveInfo> findByEmail(String email) {
        // 모든 민감 정보를 가져와서 메모리에서 복호화하여 검색
        List<UserSensitiveInfo> allSensitiveInfos = userSensitiveInfoRepository.findAll();
        
        return cryptoSearchHelper.filterByEncryptedField(
                allSensitiveInfos,
                UserSensitiveInfo::getEmail,
                email
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserSensitiveInfo> findByAddress(String address) {
        // 모든 민감 정보를 가져와서 메모리에서 복호화하여 검색
        List<UserSensitiveInfo> allSensitiveInfos = userSensitiveInfoRepository.findAll();
        
        return cryptoSearchHelper.filterByEncryptedField(
                allSensitiveInfos,
                info -> {
                    // 주소와 상세 주소를 모두 검색
                    String fullAddress = (info.getAddress() != null ? info.getAddress() : "") +
                            " " + (info.getDetailAddress() != null ? info.getDetailAddress() : "");
                    return fullAddress.trim();
                },
                address
        );
    }
}
