package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.RequiredArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.Message;
import mk.finki.ukim.lost_and_found.model.domain.MessageAttachment;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.repository.MessageAttachmentRepository;
import mk.finki.ukim.lost_and_found.repository.MessageRepository;
import mk.finki.ukim.lost_and_found.service.domain.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final int MAX_ATTACHMENTS = 5;
    private static final int MAX_ATTACHMENT_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final int MAX_CONTENT_LENGTH = 5000;
    private static final int MAX_PAGE_SIZE = 100;

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public Message send(User sender, User receiver, String content, List<byte[]> attachments) {
        if (sender.getId().equals(receiver.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a message to yourself");
        }
        if (!receiver.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The receiver's account is inactive");
        }

        String text = content == null ? "" : content.trim();
        List<byte[]> files = attachments == null ? List.of() : attachments;

        if (text.isEmpty() && files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must have content or an attachment");
        }
        if (text.length() > MAX_CONTENT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message content must not exceed " + MAX_CONTENT_LENGTH + " characters");
        }
        if (files.size() > MAX_ATTACHMENTS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At most " + MAX_ATTACHMENTS + " attachments are allowed");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(text);

        for (byte[] bytes : files) {
            if (bytes.length > MAX_ATTACHMENT_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                        "Attachment exceeds " + (MAX_ATTACHMENT_BYTES / (1024 * 1024)) + " MB");
            }
            MessageAttachment attachment = new MessageAttachment();
            attachment.setMessage(message);
            attachment.setAttachmentBytes(bytes);
            attachment.setSizeBytes(bytes.length);
            message.getAttachments().add(attachment);
        }

        return messageRepository.save(message);
    }

    @Override
    public Page<Message> findConversation(User user, User other, int pageNum, int pageSize) {
        int safePage = Math.max(pageNum, 0);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return messageRepository.findConversation(user.getId(), other.getId(),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "dateSent")));
    }

    @Override
    public List<Message> findLatestMessagePerConversation(User user) {
        return messageRepository.findLatestMessagePerConversation(user.getId());
    }

    @Override
    public Optional<Message> findById(String id, User user) {
        return messageRepository.findByIdWithUsers(id).filter(m -> isParticipant(m, user));
    }

    @Override
    public Optional<MessageAttachment> findAttachmentById(String attachmentId, User user) {
        return attachmentRepository.findByIdWithMessage(attachmentId)
                .filter(a -> isParticipant(a.getMessage(), user));
    }

    @Override
    @Transactional
    public Optional<Message> deleteById(String id, User user) {
        Optional<Message> message = findById(id, user);
        message.ifPresent(m -> {
            if (!m.getSender().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the sender can delete a message");
            }
            messageRepository.delete(m);
        });
        return message;
    }

    private boolean isParticipant(Message message, User user) {
        return message.getSender().getId().equals(user.getId())
                || message.getReceiver().getId().equals(user.getId());
    }
}