package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.Message;

import java.time.Instant;
import java.util.List;

public record MessageDto(
        String id,
        String senderId,
        String senderName,
        String senderAlias,
        String receiverId,
        String receiverName,
        String receiverAlias,
        String content,
        Instant dateSent,
        List<AttachmentDto> attachments
) {
    public record AttachmentDto(String id, int sizeBytes) {}

    public static MessageDto from(Message m) {
        return new MessageDto(
                m.getId(),
                m.getSender().getId(),
                m.getSender().getName(),
                m.getSender().getAlias(),
                m.getReceiver().getId(),
                m.getReceiver().getName(),
                m.getReceiver().getAlias(),
                m.getContent(),
                m.getDateSent(),
                m.getAttachments().stream()
                        .map(a -> new AttachmentDto(a.getId(), a.getSizeBytes()))
                        .toList()
        );
    }
}