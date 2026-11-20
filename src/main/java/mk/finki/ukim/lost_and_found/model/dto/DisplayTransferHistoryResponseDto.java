package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record DisplayTransferHistoryResponseDto(
        String id,
        String fromHolderId,
        String fromHolderName,
        String toHolderId,
        String toHolderName,
        Instant dateSent,
        Instant dateResponded,
        Boolean accepted
) {
    public static DisplayTransferHistoryResponseDto from(TransferFoundItem t, User from) {
        return new DisplayTransferHistoryResponseDto(
                t.getId(),
                from.getId(), from.getName(),
                t.getNewHolder().getId(), t.getNewHolder().getName(),
                t.getDateSent(), t.getDateResponded(), t.getAccepted());
    }

    public static List<DisplayTransferHistoryResponseDto> from(User initialHolder, List<TransferFoundItem> oldestFirst) {
        List<DisplayTransferHistoryResponseDto> entries = new ArrayList<>(oldestFirst.size());
        User from = initialHolder;

        for (TransferFoundItem t : oldestFirst) {
            entries.add(from(t, from));

            if (Boolean.TRUE.equals(t.getAccepted())) {
                from = t.getNewHolder();
            }
        }

        return entries.reversed();
    }
}