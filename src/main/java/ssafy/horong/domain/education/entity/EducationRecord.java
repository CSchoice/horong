package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.common.util.ListToStringConverter;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "education_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EducationRecord extends BaseEntity {
    // id 필드는 BaseEntity에서 상속받음

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "education_id", nullable = false)
    private Education education; // 교육 자료와 연관된 단어

    @Column(nullable = false)
    private String text; // 사용자가 말한 텍스트

    @Column(nullable = false)
    private float cer; // 인증 점수

    @Column(nullable = false)
    private LocalDate date; // 학습 날짜

    @Column(length = 255, nullable = false)
    private String audio; // S3 링크

    @Convert(converter = ListToStringConverter.class)
    private List<Integer> gtIdx;

    @Convert(converter = ListToStringConverter.class)
    private List<Integer> hypIdx;
}
