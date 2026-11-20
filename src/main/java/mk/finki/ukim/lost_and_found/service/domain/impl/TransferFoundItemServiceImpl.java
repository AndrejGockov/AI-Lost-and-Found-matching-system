package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.exceptions.TransferAlreadyPendingException;
import mk.finki.ukim.lost_and_found.model.exceptions.TransferAlreadyProcessedException;
import mk.finki.ukim.lost_and_found.model.exceptions.TransferNotFoundException;
import mk.finki.ukim.lost_and_found.repository.TransferFoundItemRepository;
import mk.finki.ukim.lost_and_found.service.domain.TransferFoundItemService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransferFoundItemServiceImpl implements TransferFoundItemService {

    private final TransferFoundItemRepository transferFoundItemRepository;

    @Override
    public List<TransferFoundItem> findHistory(FoundItem foundItem) {
        return transferFoundItemRepository.findByFoundItemIdOrderByDateSentAsc(foundItem.getId());
    }

    @Override
    public Optional<TransferFoundItem> findPendingTransfer(FoundItem foundItem) {
        return transferFoundItemRepository.findByFoundItem_IdAndAcceptedIsNull(foundItem.getId());
    }

    @Override
    public TransferFoundItem findPendingWithLockById(String transferId) {
        TransferFoundItem transfer = transferFoundItemRepository.findByIdWithLock(transferId)
                .orElseThrow(() -> new TransferNotFoundException(transferId));

        if (transfer.getAccepted() != null) {
            throw new TransferAlreadyProcessedException();
        }

        return transfer;
    }

    @Override
    public TransferFoundItem offerTransfer(FoundItem foundItem, User newHolder) {
        transferFoundItemRepository.findByFoundItem_IdAndAcceptedIsNull(foundItem.getId())
                .ifPresent(existing -> {
                    throw new TransferAlreadyPendingException();
                });

        TransferFoundItem transfer = new TransferFoundItem();
        transfer.setFoundItem(foundItem);
        transfer.setNewHolder(newHolder);

        return transferFoundItemRepository.save(transfer);
    }

    @Override
    public TransferFoundItem retractTransfer(String transferId) {
        TransferFoundItem transfer = findPendingWithLockById(transferId);
        transferFoundItemRepository.delete(transfer);
        return transfer;
    }

    @Override
    public TransferFoundItem acceptTransfer(String transferId) {
        return respond(transferId, true);
    }

    @Override
    public TransferFoundItem declineTransfer(String transferId) {
        return respond(transferId, false);
    }

    @Override
    public TransferFoundItem respond(String transferId, boolean accept) {
        TransferFoundItem transfer = findPendingWithLockById(transferId);

        transfer.setAccepted(accept);
        transfer.setDateResponded(Instant.now());

        if (accept) {
            FoundItem item = transfer.getFoundItem();
            item.setHolder(transfer.getNewHolder());
        }

        return transferFoundItemRepository.save(transfer);
    }
}