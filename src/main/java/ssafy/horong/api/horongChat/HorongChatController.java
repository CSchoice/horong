package ssafy.horong.api.horongChat;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.horongChat.request.SaveChatLogRequest;
import ssafy.horong.api.horongChat.response.HorongChatRoomListResponse;
import ssafy.horong.api.horongChat.response.HorongChatRoomResponse;
import ssafy.horong.domain.horongChat.service.HorongChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class HorongChatController {
    private final HorongChatService horongChatService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅 기록을 저장하는 API", description = "채팅 기록을 저장하는 API입니다.")
    @PostMapping("")
    public CommonResponse<Void> saveChatRecord(@Validated @RequestBody SaveChatLogRequest request) {
        horongChatService.saveChatLog(request.toCommand());
        return CommonResponse.ok("채팅이 저장되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅 목록을 조회하는 API", description = "채팅 목록을 조회하는 API입니다.")
    @GetMapping("")
    public CommonResponse<HorongChatRoomListResponse> getChatRoomList() {
        HorongChatRoomListResponse response = horongChatService.getChatRoomList();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "채팅방을 조회하는 API", description = "채팅방을 조회하는 API입니다.")
    @GetMapping("/{roomId}")
    public CommonResponse<HorongChatRoomResponse> getChatRoom(@PathVariable Long roomId) {
        HorongChatRoomResponse response = horongChatService.getChatRoom(roomId);
        return CommonResponse.ok(response);
    }
}
