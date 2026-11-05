package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.RequiredArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.Message;
import mk.finki.ukim.lost_and_found.model.domain.MessageAttachment;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.ConversationDto;
import mk.finki.ukim.lost_and_found.model.dto.MessageDto;
import mk.finki.ukim.lost_and_found.repository.MessageAttachmentRepository;
import mk.finki.ukim.lost_and_found.repository.MessageRepository;
import mk.finki.ukim.lost_and_found.service.domain.MessageService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final int MAX_ATTACHMENTS = 5;
    private static final int MAX_ATTACHMENT_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final int MAX_CONTENT_LENGTH = 5000;
    private static final int MAX_PAGE_SIZE = 100;

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository attachmentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public MessageDto send(String senderId, String receiverId, String content, List<MultipartFile> files) {
        User sender = userService.findByUsername(senderId);
        User receiver = userService.findByUsername(receiverId);

        if (sender.getId().equals(receiver.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a message to yourself");
        }
        if (!receiver.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The receiver's account is inactive");
        }

        String text = content == null ? "" : content.trim();
        List<MultipartFile> nonEmptyFiles = files == null ? List.of()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (text.isEmpty() && nonEmptyFiles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must have content or an attachment");
        }
        if (text.length() > MAX_CONTENT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message content must not exceed " + MAX_CONTENT_LENGTH + " characters");
        }
        if (nonEmptyFiles.size() > MAX_ATTACHMENTS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At most " + MAX_ATTACHMENTS + " attachments are allowed");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(text);

        for (MultipartFile file : nonEmptyFiles) {
            if (file.getSize() > MAX_ATTACHMENT_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                        "Attachment exceeds " + (MAX_ATTACHMENT_BYTES / (1024 * 1024)) + " MB");
            }
            byte[] bytes;
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read attachment");
            }
            MessageAttachment attachment = new MessageAttachment();
            attachment.setMessage(message);
            attachment.setAttachmentBytes(bytes);
            attachment.setSizeBytes(bytes.length);
            message.getAttachments().add(attachment);
        }

        return MessageDto.from(messageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversations(String userId) {
        User me = userService.findByUsername(userId);

        // keyed by partner id; the query is already ordered newest first
        Map<String, ConversationDto> conversations = new LinkedHashMap<>();
        for (Message m : messageRepository.findLatestMessagePerConversation(me.getId())) {
            User other = m.getSender().getId().equals(me.getId()) ? m.getReceiver() : m.getSender();
            conversations.putIfAbsent(other.getId(), new ConversationDto(
                    other.getId(),
                    other.getName(),
                    other.getAlias(),
                    m.getContent(),
                    m.getSender().getId(),
                    m.getDateSent(),
                    !m.getAttachments().isEmpty()
            ));
        }
        return new ArrayList<>(conversations.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getConversation(String userId, String otherUserId, int page, int size) {
        User me = userService.findByUsername(userId);
        User other = userService.findByUsername(otherUserId); // throws if the user doesn't exist

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        List<MessageDto> result = new ArrayList<>(
                messageRepository.findConversation(me.getId(), other.getId(),
                                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "dateSent")))
                        .map(MessageDto::from)
                        .getContent());
        Collections.reverse(result); // oldest first for display
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto getById(String userId, String messageId) {
        User me = userService.findByUsername(userId);
        Message message = messageRepository.findByIdWithUsers(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        requireParticipant(message, me);
        return MessageDto.from(message);
    }

    @Override
    @Transactional
    public void delete(String userId, String messageId) {
        User me = userService.findByUsername(userId);
        Message message = messageRepository.findByIdWithUsers(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        if (!message.getSender().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the sender can delete a message");
        }
        messageRepository.delete(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageAttachment getAttachment(String userId, String attachmentId) {
        User me = userService.findByUsername(userId);
        MessageAttachment attachment = attachmentRepository.findByIdWithMessage(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        requireParticipant(attachment.getMessage(), me);
        return attachment;
    }

    private void requireParticipant(Message message, User user) {
        boolean participant = message.getSender().getId().equals(user.getId())
                || message.getReceiver().getId().equals(user.getId());
        if (!participant) {
            // 404 instead of 403 so message ids can't be probed
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }
}