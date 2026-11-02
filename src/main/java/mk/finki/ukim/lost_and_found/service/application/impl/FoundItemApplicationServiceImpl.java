package mk.finki.ukim.lost_and_found.service.application.impl;

import java.util.Optional;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateFoundItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayRestrictedFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.FoundItemNotFoundException;
import mk.finki.ukim.lost_and_found.model.exceptions.OnlyHolderCanModifyPostException;
import mk.finki.ukim.lost_and_found.service.application.FoundItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.FoundItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoundItemApplicationServiceImpl implements FoundItemApplicationService {

    private final UserService userService;
    private final FoundItemService foundItemService;

    public FoundItemApplicationServiceImpl(UserService userService, FoundItemService foundItemService) {
        this.userService = userService;
        this.foundItemService = foundItemService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisplayFoundItemResponseDto> findById(String id) {
        return foundItemService
                .findById(id)
                .map(DisplayFoundItemResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisplayRestrictedFoundItemResponseDto> findByIdRestricted(String id) {
        return foundItemService
                .findById(id)
                .map(DisplayRestrictedFoundItemResponseDto::from);
    }

    @Override
    @Transactional
    public DisplayFoundItemResponseDto create(CreateOrUpdateFoundItemRequestDto requestDto, User poster) {
        User holder = userService.findByUsername(requestDto.holderId());
        FoundItem created = foundItemService.create(requestDto.toFoundItem(poster, holder));
        return DisplayFoundItemResponseDto.from(created);
    }

    @Override
    @Transactional
    public DisplayFoundItemResponseDto update(String id, CreateOrUpdateFoundItemRequestDto requestDto, User user) {
        FoundItem existing = foundItemService
                .findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        if (!existing.getHolder().getId().equals(user.getId())) {
            throw new OnlyHolderCanModifyPostException();
        }

        User holder = userService.findByUsername(requestDto.holderId());
        FoundItem updated = foundItemService.update(
                id,
                requestDto.toFoundItem(existing.getPoster(), holder)
        );

        return DisplayFoundItemResponseDto.from(updated);
    }

    @Override
    @Transactional
    public DisplayFoundItemResponseDto updateStatus(String id, ItemStatus itemStatus, User user) {
        FoundItem existing = foundItemService
                .findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        if (!existing.getHolder().getId().equals(user.getId())) {
            throw new OnlyHolderCanModifyPostException();
        }

        return DisplayFoundItemResponseDto.from(foundItemService.updateStatus(id, itemStatus));
    }

    @Override
    @Transactional
    public DisplayFoundItemResponseDto updateHolder(String id, String newHolderId, User user) {
        FoundItem existing = foundItemService
                .findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        if (!existing.getHolder().getId().equals(user.getId())) {
            throw new OnlyHolderCanModifyPostException();
        }

        return DisplayFoundItemResponseDto.from(foundItemService.updateHolder(id, newHolderId));
    }

    @Override
    @Transactional
    public Optional<DisplayFoundItemResponseDto> deleteById(String id, User user) {
        Optional<FoundItem> item = foundItemService.findById(id);

        item.ifPresent(i -> {
            if (!i.getHolder().getId().equals(user.getId())) {
                throw new OnlyHolderCanModifyPostException();
            }
        });

        Optional<DisplayFoundItemResponseDto> dto = item.map(DisplayFoundItemResponseDto::from);
        dto.ifPresent(d -> foundItemService.deleteById(id));

        return dto;
    }

    // The full/unrestricted list: items where identifiable = true.
    @Override
    @Transactional(readOnly = true)
    public Page<DisplayFoundItemResponseDto> findPage(String title, String posterName, ItemStatus status, User user, int pageNum, int pageSize) {
        if (userService.isStudent(user)) {
            return foundItemService
                    .findPage(title, true, pageNum, pageSize)
                    .map(DisplayFoundItemResponseDto::from);
        }

        return foundItemService
                .findPage(title, posterName, status, true, pageNum, pageSize)
                .map(DisplayFoundItemResponseDto::from);
    }

    // The restricted list: items where identifiable = false.
    @Override
    @Transactional(readOnly = true)
    public Page<DisplayRestrictedFoundItemResponseDto> findPageRestricted(String title, int pageNum, int pageSize) {
        return foundItemService
                .findPage(title, false, pageNum, pageSize)
                .map(DisplayRestrictedFoundItemResponseDto::from);
    }
}