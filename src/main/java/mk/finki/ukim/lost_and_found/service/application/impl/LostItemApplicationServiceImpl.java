package mk.finki.ukim.lost_and_found.service.application.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateLostItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.service.application.LostItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.LostItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LostItemApplicationServiceImpl implements LostItemApplicationService {
    private final UserService userService;
    private final LostItemService lostItemService;

    @Override
    public Optional<DisplayLostItemResponseDto> findById(String id) {
        return Optional.empty();
    }

    @Override
    public DisplayLostItemResponseDto create(CreateOrUpdateLostItemRequestDto createOrUpdateLostItemRequestDto) {
        return null;
    }

    @Override
    public DisplayLostItemResponseDto update(String id, CreateOrUpdateLostItemRequestDto updateLostItemRequestDto) {
        return null;
    }

    @Override
    public DisplayLostItemResponseDto updateStatus(String id, ItemStatus itemStatus) {
        return null;
    }

    @Override
    public Optional<DisplayLostItemResponseDto> deleteById(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DisplayLostItemResponseDto> findPage(String title, String posterName, ItemStatus status, int pageNum, int pageSize) {
        return null;
    }
}
