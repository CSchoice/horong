package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.api.chat.response.HorongChatMessageResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class ChatSseUtil {
    // userId → SSE emitters
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 클라이언트에서 호출할 구독 엔드포인트용 메서드
     */
    public SseEmitter createEmitter() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음

        // 신규 emitter 등록
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        // 완료/타임아웃/에러 시 emitter 제거
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(()   -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            // 연결 확인용 초기 이벤트
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("chat stream open"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        // keep-alive
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    emitter.send(SseEmitter.event()
                            .name("keepAlive")
                            .data("ping"));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                    timer.cancel();
                }
            }
        }, 0, 60000);

        return emitter;
    }

    /**
     * 특정 사용자에게 채팅 메시지 DTO 리스트를 푸시
     */
    public void sendChatToUser(List<HorongChatMessageResponse> messages, Long userId) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) return;

        for (SseEmitter emitter : userEmitters) {
            for (HorongChatMessageResponse msg : messages) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("chat")
                            .data(msg));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                }
            }
        }
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(userId);
        }
    }
}
