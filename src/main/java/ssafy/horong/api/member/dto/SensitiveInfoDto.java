package ssafy.horong.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.horong.domain.member.entity.UserSensitiveInfo;

/**
 * 사용자 민감 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveInfoDto {

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phoneNumber;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    
    private String address;
    
    private String detailAddress;
    
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식이 올바르지 않습니다. (예: 1990-01-01)")
    private String birthDate;
    
    /**
     * 엔티티를 DTO로 변환
     */
    public static SensitiveInfoDto from(UserSensitiveInfo entity) {
        if (entity == null) {
            return null;
        }
        
        return SensitiveInfoDto.builder()
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .detailAddress(entity.getDetailAddress())
                .birthDate(entity.getBirthDate())
                .build();
    }
}
