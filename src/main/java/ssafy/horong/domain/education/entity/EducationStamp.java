package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.member.entity.User;

@Entity
@Table(name = "EducationStamp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EducationStamp extends BaseEntity {

    // id 필드는 BaseEntity에서 상속받음

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // createdAt, updatedAt, deletedAt 필드는 BaseEntity에서 상속받음
}
