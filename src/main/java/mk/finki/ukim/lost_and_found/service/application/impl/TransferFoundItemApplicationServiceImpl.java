package mk.finki.ukim.lost_and_found.service.application.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayTransferFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.TransferHistoryEntryDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.*;
import mk.finki.ukim.lost_and_found.service.application.TransferFoundItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.FoundItemService;
import mk.finki.ukim.lost_and_found.service.domain.TransferFoundItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransferFoundItemApplicationServiceImpl implements TransferFoundItemApplicationService {
    private final UserService userService;
    private final FoundItemService foundItemService;
    private final TransferFoundItemService transferFoundItemService;

    @Override
    @Transactional(readOnly = true)
    public List<TransferHistoryEntryDto> findHistory(String foundItemId) {
        FoundItem item = foundItemService.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        List<TransferHistoryEntryDto> entries = new ArrayList<>();
        User from = item.getPoster();

        for (TransferFoundItem t : transferFoundItemService.findHistory(item)) {
            entries.add(new TransferHistoryEntryDto(
                    t.getId(),
                    from.getId(), from.getName(),
                    t.getNewHolder().getId(), t.getNewHolder().getName(),
                    t.getDateSent(), t.getDateResponded(), t.getAccepted()));

            if (Boolean.TRUE.equals(t.getAccepted())) {
                from = t.getNewHolder();
            }
        }

        return entries.reversed();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisplayTransferFoundItemResponseDto> findPendingTransfer(String foundItemId, User user) {
        FoundItem item = foundItemService.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        return transferFoundItemService.findPendingTransfer(item)
                .filter(t -> t.getNewHolder().getId().equals(user.getId())
                        || item.getHolder().getId().equals(user.getId()))
                .map(DisplayTransferFoundItemResponseDto::from);
    }

    @Override
    @Transactional
    public DisplayTransferFoundItemResponseDto offerTransfer(String foundItemId, String newHolderUsername, User user) {
        FoundItem item = foundItemService.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        // Only the holder can transfer an item
        if (!item.getHolder().getId().equals(user.getId())) {
            throw new OnlyHolderCanModifyPostException();
        }

        // Only unresolved items can be transferred
        if(!item.getStatus().equals(ItemStatus.UNRESOLVED)){
            throw new CannotTransferItemException();
        }

        User newHolder = userService.findByUsername(newHolderUsername);

        // You can't transfer an item to yourself
        if (newHolder.getId().equals(item.getHolder().getId())) {
            throw new CannotTransferToSelfException();
        }

        TransferFoundItem transfer = transferFoundItemService.offerTransfer(item, newHolder);
        return DisplayTransferFoundItemResponseDto.from(transfer);
    }

    @Override
    @Transactional
    public DisplayTransferFoundItemResponseDto retractTransfer(String foundItemId, User user) {
        FoundItem item = foundItemService.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        // Only the holder can retract a transfer offer
        if (!item.getHolder().getId().equals(user.getId())) {
            throw new OnlyHolderCanModifyPostException();
        }

        TransferFoundItem pending = transferFoundItemService.findPendingTransfer(item)
                .orElseThrow(() -> new TransferNotFoundException(foundItemId));

        return DisplayTransferFoundItemResponseDto.from(
                transferFoundItemService.retractTransfer(pending.getId()));
    }

    @Override
    @Transactional
    public DisplayTransferFoundItemResponseDto acceptTransfer(String foundItemId, User user) {
        return respond(foundItemId, user, true);
    }

    @Override
    @Transactional
    public DisplayTransferFoundItemResponseDto rejectTransfer(String foundItemId, User user) {
        return respond(foundItemId, user, false);
    }

    private DisplayTransferFoundItemResponseDto respond(String foundItemId, User user, boolean accept) {
        FoundItem item = foundItemService.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        TransferFoundItem pending = transferFoundItemService.findPendingTransfer(item)
                .orElseThrow(() -> new TransferNotFoundException(foundItemId));

        // Only the recipient can respond to an offer
        if (!pending.getNewHolder().getId().equals(user.getId())) {
            throw new NotTransferRecipientException();
        }

        TransferFoundItem result = accept
                ? transferFoundItemService.acceptTransfer(pending.getId())
                : transferFoundItemService.declineTransfer(pending.getId());

        return DisplayTransferFoundItemResponseDto.from(result);
    }
}
