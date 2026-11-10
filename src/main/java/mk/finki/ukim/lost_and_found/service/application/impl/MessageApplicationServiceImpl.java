package mk.finki.ukim.lost_and_found.service.application.impl;

import lombok.RequiredArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.Message;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayConversationResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayMessageAttachmentResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayMessageResponseDto;
import mk.finki.ukim.lost_and_found.service.application.MessageApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.MessageService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import mk.finki.ukim.lost_and_found.model.dto.DisplayUserResponseDto;
import mk.finki.ukim.lost_and_found.model.exceptions.InvalidUsernameException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageApplicationServiceImpl implements MessageApplicationService {

    private final MessageService messageService;
    private final UserService userService;

    @Override
    @Transactional
    public DisplayMessageResponseDto send(String receiverId, String content, List<MultipartFile> files, User sender) {
        User receiver = userService.findByUsername(receiverId);

        List<byte[]> attachments = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                try {
                    attachments.add(file.getBytes());
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read attachment");
                }
            }
        }

        return DisplayMessageResponseDto.from(messageService.send(sender, receiver, content, attachments));
    }

    @Override
    public List<DisplayConversationResponseDto> findConversations(User user) {
        // keyed by partner id; the domain query is already ordered newest first
        Map<String, DisplayConversationResponseDto> conversations = new LinkedHashMap<>();
        for (Message m : messageService.findLatestMessagePerConversation(user)) {
            User other = m.getSender().getId().equals(user.getId()) ? m.getReceiver() : m.getSender();
            conversations.putIfAbsent(other.getId(), new DisplayConversationResponseDto(
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
    public Page<DisplayMessageResponseDto> findConversation(String otherUserId, User user, int pageNum, int pageSize) {
        User other = userService.findByUsername(otherUserId);
        return messageService.findConversation(user, other, pageNum, pageSize)
                .map(DisplayMessageResponseDto::from);
    }

    @Override
    public Optional<DisplayMessageResponseDto> findById(String id, User user) {
        return messageService.findById(id, user).map(DisplayMessageResponseDto::from);
    }

    @Override
    public Optional<DisplayMessageAttachmentResponseDto> findAttachmentById(String attachmentId, User user) {
        return messageService.findAttachmentById(attachmentId, user).map(a -> {
            byte[] bytes = a.getAttachmentBytes();
            String contentType;
            try {
                contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                contentType = null;
            }
            return new DisplayMessageAttachmentResponseDto(
                    a.getId(), bytes, contentType != null ? contentType : "application/octet-stream");
        });
    }

    @Override
    @Transactional
    public Optional<DisplayMessageResponseDto> deleteById(String id, User user) {
        return messageService.deleteById(id, user).map(DisplayMessageResponseDto::from);
    }
    @Override
    public List<DisplayUserResponseDto> searchUsers(String query, User user) {
        return userService.searchActive(query, user, 10).stream()
                .map(DisplayUserResponseDto::from)
                .toList();
    }

    @Override
    public Optional<DisplayUserResponseDto> findUser(String id) {
        try {
            return Optional.of(DisplayUserResponseDto.from(userService.findByUsername(id)));
        } catch (InvalidUsernameException e) {
            return Optional.empty();
        }
    }
}