package mk.finki.ukim.lost_and_found.service.application;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayConversationResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayMessageAttachmentResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayMessageResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MessageApplicationService {

    DisplayMessageResponseDto send(String receiverId, String content, List<MultipartFile> files, User sender);

    List<DisplayConversationResponseDto> findConversations(User user);

    /** Newest first. */
    Page<DisplayMessageResponseDto> findConversation(String otherUserId, User user, int pageNum, int pageSize);

    Optional<DisplayMessageResponseDto> findById(String id, User user);

    Optional<DisplayMessageAttachmentResponseDto> findAttachmentById(String attachmentId, User user);

    Optional<DisplayMessageResponseDto> deleteById(String id, User user);
    List<DisplayUserResponseDto> searchUsers(String query, User user);

    Optional<DisplayUserResponseDto> findUser(String id);
}