package mk.finki.ukim.lost_and_found.service.application;

import java.util.Optional;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateFoundItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayRestrictedFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import org.springframework.data.domain.Page;

public interface FoundItemApplicationService {

    Optional<DisplayFoundItemResponseDto> findById(String id);

    Optional<DisplayRestrictedFoundItemResponseDto> findByIdRestricted(String id);

    DisplayFoundItemResponseDto create(CreateOrUpdateFoundItemRequestDto requestDto, User poster);

    DisplayFoundItemResponseDto update(String id, CreateOrUpdateFoundItemRequestDto requestDto, User user);

    DisplayFoundItemResponseDto updateStatus(String id, ItemStatus itemStatus, User user);

    DisplayFoundItemResponseDto updateHolder(String id, String newHolderId, User user);

    Optional<DisplayFoundItemResponseDto> deleteById(String id, User user);

    // Students: status is always UNRESOLVED
    // Employees: UNRESOLVED by default, or the given status
    Page<DisplayFoundItemResponseDto> findPage(String title, String posterName, ItemStatus status, User user, int pageNum, int pageSize);

    Page<DisplayRestrictedFoundItemResponseDto> findPageRestricted(String title, int pageNum, int pageSize);
}