package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;

public record DisplayConversationResponseDto(
        String otherUserId,
        String otherUserName,
        String otherUserAlias,
        String lastMessageContent,
        String lastMessageSenderId,
        Instant lastMessageDate,
        boolean lastMessageHasAttachments
) {}