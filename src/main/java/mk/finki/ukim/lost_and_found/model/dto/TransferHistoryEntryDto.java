package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;

// A view model for the "full history" page only. "fromHolder" is not a
// stored column: the table has no current_holder_id, so it's derived in
// TransferFoundItemApplicationServiceImpl.findHistory by walking the
// transfers oldest-to-newest, starting from the item's poster and moving
// the "current" holder forward only on each ACCEPTED transfer (a pending
// or rejected transfer leaves the real holder unchanged).
public record TransferHistoryEntryDto(
        String id,
        String fromHolderId,
        String fromHolderName,
        String toHolderId,
        String toHolderName,
        Instant dateSent,
        Instant dateResponded,
        Boolean accepted
) {
}
