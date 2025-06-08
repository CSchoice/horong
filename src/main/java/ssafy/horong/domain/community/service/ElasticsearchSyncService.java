package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.domain.community.elastic.PostDocument;
import ssafy.horong.domain.community.elastic.PostElasticsearchRepository;
import ssafy.horong.domain.community.entity.ContentByLanguage;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.community.repository.PostRepository;
import ssafy.horong.domain.member.common.Language;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncService {

    private static final String KOREAN = "KOREAN";
    private static final String CHINESE = "CHINESE";
    private static final String JAPANESE = "JAPANESE";
    private static final String ENGLISH = "ENGLISH";

    private final PostRepository postRepository;
    private final PostElasticsearchRepository postElasticsearchRepository;

    public void syncMissingPostsToElasticsearch() {
        try {
            log.info("Elasticsearch 동기화 시작");

            Set<Long> indexedPostIds = postElasticsearchRepository.findAll().stream()
                    .map(PostDocument::getPostId)
                    .collect(Collectors.toSet());

            // 삭제되지 않은 게시물만 동기화 대상으로 선택
            List<Post> postsToIndex = postRepository.findAllActivePost().stream()
                    .filter(post -> !indexedPostIds.contains(post.getId()))
                    .toList();

            log.info("동기화 대상 게시물 수: {}", postsToIndex.size());

            for (Post post : postsToIndex) {
                try {
                    indexPost(post);
                } catch (Exception e) {
                    log.error("게시물 인덱싱 중 오류 발생: postId={}, 오류={}", post.getId(), e.getMessage(), e);
                }
            }

            log.info("Elasticsearch 동기화 완료");
        } catch (Exception e) {
            log.error("Elasticsearch 동기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void indexPost(Post post) {
        List<CreateContentByLanguageRequest> contentList = post.getContentByCountries().stream()
                .collect(Collectors.groupingBy(ContentByLanguage::getLanguage))
                .entrySet().stream()
                .map(entry -> {
                    Language language = entry.getKey();
                    String title = entry.getValue().stream()
                            .filter(c -> c.getContentType() == ContentByLanguage.ContentType.TITLE)
                            .map(ContentByLanguage::getContent)
                            .findFirst().orElse(null);
                    String content = entry.getValue().stream()
                            .filter(c -> c.getContentType() == ContentByLanguage.ContentType.CONTENT)
                            .map(ContentByLanguage::getContent)
                            .findFirst().orElse(null);
                    boolean isOriginal = entry.getValue().stream().anyMatch(ContentByLanguage::isOriginal);

                    return new CreateContentByLanguageRequest(language, title, content, isOriginal);
                })
                .toList();

        PostDocument postDocument = PostDocument.builder()
                .postId(post.getId())
                .author(post.getAuthor().getNickname())
                .authorId(post.getAuthor().getId())
                .build();

        for (CreateContentByLanguageRequest c : contentList) {
            if (c.language() == null) {
                log.warn("language가 null인 콘텐츠 발견: postId={}", post.getId());
                continue;
            }
            
            String language = c.language().name();
            switch (language) {
                case KOREAN -> {
                    postDocument.setTitleKo(c.title());
                    postDocument.setContentKo(c.content());
                }
                case CHINESE -> {
                    postDocument.setTitleZh(c.title());
                    postDocument.setContentZh(c.content());
                }
                case JAPANESE -> {
                    postDocument.setTitleJa(c.title());
                    postDocument.setContentJa(c.content());
                }
                case ENGLISH -> {
                    postDocument.setTitleEn(c.title());
                    postDocument.setContentEn(c.content());
                }
                default -> log.warn("지원하지 않는 언어: {}", language);
            }
        }

        postElasticsearchRepository.save(postDocument);
        log.info("인덱싱 완료: postId={}", post.getId());
    }
}
