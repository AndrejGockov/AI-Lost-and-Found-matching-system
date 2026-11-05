package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;

import java.util.List;

public interface TransferFoundItemService {
    TransferFoundItem offerTransfer(FoundItem foundItem, User newHolder);

    TransferFoundItem retractTransfer(String transferId);

    TransferFoundItem acceptTransfer(String transferId);

    TransferFoundItem declineTransfer(String transferId);
}