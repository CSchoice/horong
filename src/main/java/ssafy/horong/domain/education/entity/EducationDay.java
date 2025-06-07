package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

@Entity
@Table(name = "education_day")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EducationDay extends BaseEntity {

    // id 필드는 BaseEntity에서 상속받음
    // 주의: BaseEntity의 id는 Long 타입이지만 여기서는 int 타입을 사용했엀음

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "education_day_words", joinColumns = @JoinColumn(name = "education_day_id"))
    @Column(name = "word_id")
    private List<Integer> wordIds;

    @Column(name = "day")
    private int day;

    // createdAt, updatedAt, deletedAt 필드는 BaseEntity에서 상속받음
}
