package mk.finki.ukim.lost_and_found.service.application;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateLostItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface LostItemApplicationService {
    Optional<DisplayLostItemResponseDto> findById(String id);

    DisplayLostItemResponseDto create(CreateOrUpdateLostItemRequestDto createOrUpdateLostItemRequestDto, User user);

    DisplayLostItemResponseDto update(String id, CreateOrUpdateLostItemRequestDto updateLostItemRequestDto, User user);

    DisplayLostItemResponseDto updateStatus(String id, ItemStatus itemStatus, User user);

    Optional<DisplayLostItemResponseDto> deleteById(String id, User user);

    // Students: status is always UNRESOLVED
    // Employees: UNRESOLVED by default, or the given status
    Page<DisplayLostItemResponseDto> findPage(String title, String posterName, ItemStatus status, User user, int pageNum, int pageSize);
}