package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    // 8바이트 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 날짜 필드 (8바이트)
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // 참조 타입 필드 (8바이트 참조)
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Post board;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<ContentByLanguage> contentByCountries;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
