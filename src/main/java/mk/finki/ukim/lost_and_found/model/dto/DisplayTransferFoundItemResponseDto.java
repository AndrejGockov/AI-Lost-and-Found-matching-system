package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;

public record DisplayTransferFoundItemResponseDto(
        String id,
        String newHolderId,
        String newHolderName,
        Instant dateSent,
        Instant dateResponded,
        Boolean accepted
) {
    public static DisplayTransferFoundItemResponseDto from(TransferFoundItem transfer) {
        return new DisplayTransferFoundItemResponseDto(
                transfer.getId(),
                transfer.getNewHolder().getId(),
                transfer.getNewHolder().getName(),
                transfer.getDateSent(),
                transfer.getDateResponded(),
                transfer.getAccepted()
        );
    }

    public static List<DisplayTransferFoundItemResponseDto> from(List<TransferFoundItem> transfers) {
        return transfers
                .stream()
                .map(DisplayTransferFoundItemResponseDto::from)
                .toList();
    }
}