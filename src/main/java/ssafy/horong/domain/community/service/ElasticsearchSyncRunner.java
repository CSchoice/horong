package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchSyncRunner implements ApplicationListener<ApplicationReadyEvent> {

    private static final String DEV_PROFILE = "dev";
    private static final String LOCAL_PROFILE = "local";
    private static final String TEST_PROFILE = "test";

    private final ElasticsearchSyncService syncService;
    private final Environment environment;

    @Async
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // 개발 환경에서는 로그만 출력하고 실행하지 않도록 설정
            if (isNonProductionProfile()) {
                log.info("개발/테스트 환경에서는 자동 Elasticsearch 동기화를 건너뜁니다. 프로파일: {}", 
                        Arrays.toString(environment.getActiveProfiles()));
                return;
            }
            
            log.info("엘라스틱서치 동기화 시작 - 비동기 처리");
            
            // 비동기로 Elasticsearch 동기화 실행
            CompletableFuture.runAsync(() -> {
                try {
                    syncService.syncMissingPostsToElasticsearch();
                    log.info("엘라스틱서치 동기화 완료");
                } catch (Exception e) {
                    log.error("엘라스틱서치 동기화 비동기 처리 중 오류 발생: {}", e.getMessage(), e);
                }
            }).exceptionally(ex -> {
                log.error("엘라스틱서치 동기화 비동기 처리 중 예외 발생: {}", ex.getMessage(), ex);
                return null;
            });
            
        } catch (Exception e) {
            log.error("엘라스틱서치 동기화 실행 준비 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    private boolean isNonProductionProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles)
                .anyMatch(profile -> 
                        profile.equals(DEV_PROFILE) || 
                        profile.equals(LOCAL_PROFILE) || 
                        profile.equals(TEST_PROFILE));
    }
}
