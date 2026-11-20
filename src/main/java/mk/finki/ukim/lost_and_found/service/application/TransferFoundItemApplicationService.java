package mk.finki.ukim.lost_and_found.service.application;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayTransferFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayTransferHistoryDto;

import java.util.List;
import java.util.Optional;

public interface TransferFoundItemApplicationService {

    List<DisplayTransferHistoryDto> findHistory(String foundItemId);

    Optional<DisplayTransferFoundItemResponseDto> findPendingTransfer(String foundItemId, User user);

    DisplayTransferFoundItemResponseDto offerTransfer(String foundItemId, String newHolderUsername, User user);

    DisplayTransferFoundItemResponseDto retractTransfer(String foundItemId, User user);

    DisplayTransferFoundItemResponseDto acceptTransfer(String foundItemId, User user);

    DisplayTransferFoundItemResponseDto rejectTransfer(String foundItemId, User user);
}