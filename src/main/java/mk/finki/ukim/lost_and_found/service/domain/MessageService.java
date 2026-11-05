package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.MessageAttachment;
import mk.finki.ukim.lost_and_found.model.dto.ConversationDto;
import mk.finki.ukim.lost_and_found.model.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    MessageDto send(String senderId, String receiverId, String content, List<MultipartFile> files);

    List<ConversationDto> getConversations(String userId);

    /** Messages of a conversation, oldest first within the requested page (page 0 = newest messages). */
    List<MessageDto> getConversation(String userId, String otherUserId, int page, int size);

    MessageDto getById(String userId, String messageId);

    void delete(String userId, String messageId);

    MessageAttachment getAttachment(String userId, String attachmentId);
}