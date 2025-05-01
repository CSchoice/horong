package ssafy.horong.domain.shortform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.shortform.response.ShortFormListResponse;
import ssafy.horong.api.shortform.response.ShortFormResponse;
import ssafy.horong.common.exception.data.DataNotFoundException;
import ssafy.horong.common.properties.WebClientProperties;
import ssafy.horong.common.util.UserUtil;
import ssafy.horong.domain.shortform.command.ModifyIsSavedCommand;
import ssafy.horong.domain.shortform.command.ModifyPreferenceCommand;
import ssafy.horong.domain.shortform.command.SaveShortFormLogCommand;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShortFormServiceImpl implements ShortFormService {
    // API 엔드포인트 상수
    private static final String ENDPOINT_SHORTFORM = "/shortform/";
    private static final String ENDPOINT_PREFERENCE = "/shortform/preference/";
    private static final String ENDPOINT_IS_SAVED = "/shortform/is_saved/";
    private static final String ENDPOINT_LOG = "/shortform/log";
    
    // 요청/응답 파라미터 이름 상수
    private static final String PARAM_SHORTFORM_ID = "shortform_id";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_PREFERENCE = "preference";
    private static final String PARAM_IS_SAVED = "is_saved";
    private static final String PARAM_START_AT = "start_at";
    private static final String PARAM_END_AT = "end_at";
    
    // 날짜 포맷 상수
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    // 응답 메시지 상수
    private static final String SUCCESS_LOG_SAVE = "로그 저장에 성공했습니다.";
    private static final String SUCCESS_PREFERENCE = "좋아요/싫어요 반영에 성공했습니다.";
    private static final String SUCCESS_IS_SAVED = "스크랩 반영에 성공했습니다.";
    private static final String DEFAULT_ERROR_MESSAGE = "Unknown error";
    
    private final WebClient webClient;
    private final WebClientProperties webClientProperties;
    private final UserUtil userUtil;
    
    private String getDefaultErrorMessage() {
        return DEFAULT_ERROR_MESSAGE;
    }
    
    /**
     * 웹 클라이언트 GET 요청을 수행하는 통합 메서드
     * @param endpoint API 엔드포인트
     * @param responseType 응답 타입 클래스
     * @param <T> 응답 타입
     * @return 응답 객체
     */
    private <T> T executeGetRequest(String endpoint, Class<T> responseType) {
        String requestUrl = webClientProperties.url() + endpoint;
        log.info("GET 요청 URL: {}", requestUrl);
        
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty(getDefaultErrorMessage())
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(responseType)
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);
    }
    
    /**
     * 웹 클라이언트 GET 요청을 수행하여 리스트 응답을 가져오는 통합 메서드
     * @param endpoint API 엔드포인트
     * @param elementType 리스트 요소 타입 클래스
     * @param <T> 리스트 요소 타입
     * @return 응답 객체 리스트
     */
    private <T> List<T> executeGetRequestForList(String endpoint, Class<T> elementType) {
        String requestUrl = webClientProperties.url() + endpoint;
        log.info("GET 요청 URL: {}", requestUrl);
        
        List<T> response = webClient.get()
                .uri(requestUrl)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty(getDefaultErrorMessage())
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToFlux(elementType)
                .collectList()
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        log.info("응답: {}", response);
        return response;
    }
    
    /**
     * 웹 클라이언트 POST 요청을 수행하는 통합 메서드
     * @param endpoint API 엔드포인트
     * @param requestBody 요청 바디
     * @return 응답 문자열
     */
    private String executePostRequest(String endpoint, Map<String, Object> requestBody) {
        String requestUrl = webClientProperties.url() + endpoint;
        log.info("POST 요청 URL: {}, 바디: {}", requestUrl, requestBody);
        
        String response = webClient.post()
                .uri(requestUrl)
                .body(Mono.just(requestBody), HashMap.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty(getDefaultErrorMessage())
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)))
                )
                .bodyToMono(String.class)
                .block();

        log.info("응답: {}", response);
        return response;
    }

    public List<ShortFormResponse> getShortFormList() {
        Long userId = userUtil.getCurrentUser().getId();
        return executeGetRequestForList(ENDPOINT_SHORTFORM + userId, ShortFormResponse.class);
    }

    public List<ShortFormResponse> getPreferenceList() {
        Long userId = userUtil.getCurrentUser().getId();
        return executeGetRequestForList(ENDPOINT_PREFERENCE + userId, ShortFormResponse.class);
    }

    public List<ShortFormResponse> getLikedList() {
        Long userId = userUtil.getCurrentUser().getId();
        return executeGetRequestForList(ENDPOINT_IS_SAVED + userId, ShortFormResponse.class);
    }

    public ShortFormListResponse getShortFormDetail(Long shortFormId) {
        String endpoint = ENDPOINT_SHORTFORM + userUtil.getCurrentUser().getId() + "/" + shortFormId;
        return executeGetRequest(endpoint, ShortFormListResponse.class);
    }

    public String saveShortFormLog(SaveShortFormLogCommand command) {
        Long userId = userUtil.getCurrentUser().getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(PARAM_SHORTFORM_ID, command.shortFormId());
        requestBody.put(PARAM_USER_ID, userId);
        requestBody.put(PARAM_START_AT, command.startAt().format(formatter));
        requestBody.put(PARAM_END_AT, command.endAt().format(formatter));
        
        executePostRequest(ENDPOINT_LOG, requestBody);
        return SUCCESS_LOG_SAVE;
    }

    // 숏폼 좋아요/싫어요 수정
    public String modifyPreference(ModifyPreferenceCommand command) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(PARAM_SHORTFORM_ID, command.shortFormId());
        requestBody.put(PARAM_USER_ID, userUtil.getCurrentUser().getId());
        requestBody.put(PARAM_PREFERENCE, command.preference());
        
        executePostRequest(ENDPOINT_PREFERENCE.replace("/", ""), requestBody);
        return SUCCESS_PREFERENCE;
    }

    // 숏폼 스크랩 여부 수정
    public String modifyIsSaved(ModifyIsSavedCommand command) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(PARAM_SHORTFORM_ID, command.shortFormId());
        requestBody.put(PARAM_USER_ID, userUtil.getCurrentUser().getId());
        requestBody.put(PARAM_IS_SAVED, command.isSaved());
        
        executePostRequest(ENDPOINT_IS_SAVED.replace("/", ""), requestBody);
        return SUCCESS_IS_SAVED;
    }
}