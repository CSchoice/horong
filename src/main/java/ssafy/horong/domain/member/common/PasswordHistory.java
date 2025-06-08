package ssafy.horong.domain.member.common;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.member.entity.User;

@Getter
@Builder
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordHistory extends BaseEntity {

    // id 필드는 BaseEntity에서 상속받음

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String password;

    // createdAt, updatedAt, deletedAt 필드는 BaseEntity에서 상속받음

    public void setPasswordHistory(User user, String password) {
        this.user = user;
        this.password = password;
    }
}
