package ssafy.horong.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.common.crypto.StringEncryptConverter;

import java.time.LocalDateTime;

/**
 * 사용자의 민감한 개인정보를 저장하는 엔티티
 * 모든 민감 정보는 데이터베이스에 암호화되어 저장됨
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_sensitive_info")
public class UserSensitiveInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 전화번호 (암호화)
    @Convert(converter = StringEncryptConverter.class)
    @Column(length = 255)
    private String phoneNumber;

    // 이메일 (암호화)
    @Convert(converter = StringEncryptConverter.class)
    @Column(length = 255)
    private String email;

    // 주소 (암호화)
    @Convert(converter = StringEncryptConverter.class)
    @Column(length = 255)
    private String address;

    // 상세 주소 (암호화)
    @Convert(converter = StringEncryptConverter.class)
    @Column(length = 255)
    private String detailAddress;

    // 생년월일 (암호화)
    @Convert(converter = StringEncryptConverter.class)
    @Column(length = 255)
    private String birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public UserSensitiveInfo(User user, String phoneNumber, String email, String address, String detailAddress, String birthDate) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.detailAddress = detailAddress;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 사용자 민감 정보 업데이트
     */
    public void updateInfo(String phoneNumber, String email, String address, String detailAddress, String birthDate) {
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (email != null) this.email = email;
        if (address != null) this.address = address;
        if (detailAddress != null) this.detailAddress = detailAddress;
        if (birthDate != null) this.birthDate = birthDate;
        this.updatedAt = LocalDateTime.now();
    }
}
