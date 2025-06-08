package ssafy.horong.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.common.constant.global.S3Image;
import ssafy.horong.common.crypto.StringEncryptConverter;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.MemberRole;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)  // public 기본 생성자
@AllArgsConstructor(access = AccessLevel.PROTECTED)  // 모든 필드를 포함한 생성자 (protected)
public class User extends BaseEntity implements SoftDeletable {

    // 8바이트 필드와 날짜 필드는 BaseEntity에서 상속받음

    // 문자열 필드 (참조 8바이트)
    @Column(nullable = false, length = 16)
    private String userId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 66)
    private String password; // 비밀번호는 8~20자까지 설정 가능

    @Column(length = 40)
    private String profileImg; // s3 링크 저장

    // 참조 타입 필드 (8바이트 참조)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> boards;

    // enum 필드 (4바이트 또는 참조 크기)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language; // enum 타입

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // SoftDeletable 인터페이스를 구현하여 논리적 삭제 기능 제공

    @Builder
    public User(String password, String nickname, String image) {
        this.password = password;
        this.nickname = nickname;
        this.profileImg = image;
        // deletedAt은 null로 초기화되어 있음
    }

    // 명시적인 생성자 추가 (null 값 허용)
    public User(MemberRole role) {
        // deletedAt은 null로 초기화되어 있음
        this.role = role;
    }

    public void signupMember(MemberSignupCommand signupCommand,  String password, Language language) {
        this.password = password;
        this.nickname = signupCommand.nickname();
        this.userId = signupCommand.userId();
        // deletedAt은 null로 초기화되어 있음
        this.role = MemberRole.USER;
        this.language = language;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 사용자를 논리적으로 삭제합니다.
     * SoftDeletable 인터페이스의 softDelete 메서드를 사용합니다.
     */
    public void delete() {
        this.softDelete();
    }
    
    /**
     * 사용자가 삭제되었는지 확인합니다.
     * 
     * @return 삭제되었으면 true, 아니면 false
     */
    public boolean isDeleted() {
        return SoftDeletable.super.isDeleted();
    }
    
    /**
     * 삭제 시간을 설정합니다.
     * SoftDeletable 인터페이스 구현
     * 
     * @param deletedAt 삭제 시간
     */
    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        super.deletedAt = deletedAt;
    }
    
    /**
     * 삭제 시간을 가져옵니다.
     * SoftDeletable 인터페이스 구현
     * 
     * @return 삭제 시간
     */
    @Override
    public LocalDateTime getDeletedAt() {
        return super.deletedAt;
    }
    
    /**
     * 사용자를 논리적으로 삭제합니다.
     * SoftDeletable 인터페이스의 기본 구현을 사용합니다.
     */
    @Override
    public void softDelete() {
        SoftDeletable.super.softDelete();
    }

    // 저장 전에 기본 role 설정
    @PrePersist
    public void prePersist() {
        if (profileImg == null) {
            profileImg = S3Image.DEFAULT_URL;
        }
        // createdAt은 BaseEntity에서 처리됨
    }
}