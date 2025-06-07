package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.member.common.Language;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentByLanguage extends BaseEntity {

    // 8바이트 필드와 날짜 필드는 BaseEntity에서 상속받음

    // 참조 타입 필드 (8바이트 참조)
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(length = 1000)
    private String content;

    // 기본적으로 변경 가능한 리스트로 초기화
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContentImage> contentImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Language language;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    // 1바이트 필드 (마지막에 배치하여 패딩 최소화)
    private boolean isOriginal;

    public enum ContentType {
        TITLE, CONTENT
    }
}
