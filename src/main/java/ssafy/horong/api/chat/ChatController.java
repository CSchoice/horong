package ssafy.horong.api.chat;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.chat.request.SaveChatHistoryRequest;
import ssafy.horong.api.chat.response.HorongChatRoomListResponse;
import ssafy.horong.api.chat.response.HorongChatRoomResponse;
import ssafy.horong.common.util.ChatSseUtil;
import ssafy.horong.domain.chat.service.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatSseUtil chatSseUtil;  // SSE 유틸 주입

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅 SSE 구독", description = "실시간 채팅 이벤트를 구독합니다.")
    @GetMapping("/sse")
    public SseEmitter subscribeChat() {
        return chatSseUtil.createEmitter();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅 기록을 저장하는 API", description = "채팅 기록을 저장하는 API입니다.")
    @PostMapping("")
    public CommonResponse<Void> saveChatHistory(@Validated @RequestBody SaveChatHistoryRequest request) {
        chatService.saveChatLog(request.toCommand());
        return CommonResponse.ok("채팅이 저장되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅 목록을 조회하는 API", description = "채팅 목록을 조회하는 API입니다.")
    @GetMapping("")
    public CommonResponse<HorongChatRoomListResponse> getChatRoomList() {
        HorongChatRoomListResponse response = chatService.getChatRoomList();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅방을 조회하는 API", description = "채팅방을 조회하는 API입니다.")
    @GetMapping("/{roomId}")
    public CommonResponse<HorongChatRoomResponse> getChatRoom(@PathVariable Long roomId) {
        HorongChatRoomResponse response = chatService.getChatRoom(roomId);
        return CommonResponse.ok(response);
    }
}
