package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentImage {

    // 8바이트 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 참조 타입 필드 (8바이트 참조)
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private ContentByLanguage content;
    
    // 문자열 필드 (참조 8바이트)
    @Column(length = 65)
    private String imageUrl;
}
