package mk.finki.ukim.lost_and_found.service.application.impl;

import java.util.Optional;
import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateLostItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.LostItemNotFoundException;
import mk.finki.ukim.lost_and_found.model.exceptions.OnlyPosterCanModifyPostException;
import mk.finki.ukim.lost_and_found.service.application.LostItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.LostItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LostItemApplicationServiceImpl implements LostItemApplicationService {
    private final UserService userService;
    private final LostItemService lostItemService;

    public LostItemApplicationServiceImpl(UserService userService, LostItemService lostItemService) {
        this.userService = userService;
        this.lostItemService = lostItemService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisplayLostItemResponseDto> findById(String id) {
        return lostItemService
                .findById(id)
                .map(DisplayLostItemResponseDto::from);
    }

    @Override
    @Transactional
    public DisplayLostItemResponseDto create(CreateOrUpdateLostItemRequestDto createOrUpdateLostItemRequestDto, User user) {
        return DisplayLostItemResponseDto.from(
                lostItemService.create(
                        createOrUpdateLostItemRequestDto.toLostItem(user)
                )
        );
    }

    @Override
    @Transactional
    public DisplayLostItemResponseDto update(String id, CreateOrUpdateLostItemRequestDto updateLostItemRequestDto, User user) {
        LostItem existing = lostItemService
                .findById(id)
                .orElseThrow(() -> new LostItemNotFoundException(id));

        if (!existing.getPoster().getId().equals(user.getId())) {
            throw new OnlyPosterCanModifyPostException();
        }

        return DisplayLostItemResponseDto.from(
                lostItemService.update(id,
                        updateLostItemRequestDto.toLostItem(user)
                )
        );
    }

    @Override
    @Transactional
    public DisplayLostItemResponseDto updateStatus(String id, ItemStatus itemStatus, User user) {
        LostItem existing = lostItemService
                .findById(id)
                .orElseThrow(() -> new LostItemNotFoundException(id));

        if (!existing.getPoster().getId().equals(user.getId())) {
            throw new OnlyPosterCanModifyPostException();
        }

        return DisplayLostItemResponseDto.from(lostItemService.updateStatus(id, itemStatus));
    }

    @Override
    @Transactional
    public Optional<DisplayLostItemResponseDto> deleteById(String id, User user) {
        Optional<LostItem> item = lostItemService.findById(id);

        item.ifPresent(i -> {
            if (!i.getPoster().getId().equals(user.getId())) {
                throw new OnlyPosterCanModifyPostException();
            }
        });

        // map to the DTO first, while the item and its images can still be loaded
        Optional<DisplayLostItemResponseDto> dto = item.map(DisplayLostItemResponseDto::from);
        dto.ifPresent(d -> lostItemService.deleteById(id));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayLostItemResponseDto> findPage(String title, String posterName, ItemStatus status, User user, int pageNum, int pageSize) {
        if (userService.isStudent(user)) {
            return lostItemService
                    .findPage(title, pageNum, pageSize)
                    .map(DisplayLostItemResponseDto::from);
        }

        return lostItemService
                .findPage(title, posterName, status, pageNum, pageSize)
                .map(DisplayLostItemResponseDto::from);
    }
}