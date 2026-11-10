package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.Message;
import mk.finki.ukim.lost_and_found.model.domain.MessageAttachment;
import mk.finki.ukim.lost_and_found.model.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message send(User sender, User receiver, String content, List<byte[]> attachments);

    /** Newest first. */
    Page<Message> findConversation(User user, User other, int pageNum, int pageSize);

    List<Message> findLatestMessagePerConversation(User user);

    /** Empty if the message doesn't exist or the user is not its sender/receiver. */
    Optional<Message> findById(String id, User user);

    /** Empty if the message doesn't exist or the user is not its sender/receiver. */
    Optional<MessageAttachment> findAttachmentById(String attachmentId, User user);

    /** Only the sender may delete. Empty if the message doesn't exist or the user is not a participant. */
    Optional<Message> deleteById(String id, User user);
}