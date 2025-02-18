package ssafy.horong.domain.community.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.response.*;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.ChatRoom;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.member.entity.User;

import java.util.List;
import java.util.Map;

public interface CommunityService {
    void createPost(CreatePostCommand command);
    void updatePost(UpdatePostCommand command);
    void deletePost(Long id);
    GetPostResponse getPostById(Long id);
    Page<GetPostResponse> getPostList(Pageable pageable, String boardType);
    Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable);
    void createComment(CreateCommentCommand command);
    void updateComment(UpdateCommentCommand command);
    void deleteComment(Long commentId);
    void sendMessage(SendMessageCommand command);
    GetPostIdAndMessageListResponse getMessageList(GetMessageListCommand command);
    List<GetAllMessageListResponse> getAllMessageList();
    String saveImageToS3(MultipartFile file);
    Map<BoardType, List<GetPostResponse>> getMainPostList();
    GetOriginPostResponse getOriginalPost(Long id);
    GetCommentResponse getOriginalComment(Long commentId);
    ChatRoom createChatRoom(Long userId, Long postId);
}
