package mk.finki.ukim.lost_and_found.service.application.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayItemResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.enums.ItemType;
import mk.finki.ukim.lost_and_found.service.application.FoundItemApplicationService;
import mk.finki.ukim.lost_and_found.service.application.LostItemApplicationService;
import mk.finki.ukim.lost_and_found.service.application.MyItemsApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MyItemsApplicationServiceImpl implements MyItemsApplicationService {

    private final FoundItemApplicationService foundItemApplicationService;
    private final LostItemApplicationService lostItemApplicationService;

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayItemResponseDto> findPage(User user, String title, ItemStatus status,
                                                 ItemType type, int pageNum, int pageSize) {

        List<DisplayFoundItemResponseDto> foundItems =
                type == null || type == ItemType.FOUND
                ? foundItemApplicationService.findAllByUser(user)
                : List.of();

        List<DisplayLostItemResponseDto> lostItems =
                type == null || type == ItemType.LOST
                ? lostItemApplicationService.findAllByUser(user)
                : List.of();

        String titleFilter = title == null ? null : title.trim().toLowerCase();

        List<DisplayItemResponseDto> merged = Stream.concat(
                        foundItems.stream().map(DisplayItemResponseDto::from),
                        lostItems.stream().map(DisplayItemResponseDto::from))
                .filter(i -> status == null || i.status() == status)
                .filter(i -> titleFilter == null || titleFilter.isBlank()
                        || i.title().toLowerCase().contains(titleFilter))
                .sorted(Comparator.comparing(DisplayItemResponseDto::lastUpdated).reversed())
                .toList();

        int from = Math.min(pageNum * pageSize, merged.size());
        int to = Math.min(from + pageSize, merged.size());

        return new PageImpl<>(merged.subList(from, to), PageRequest.of(pageNum, pageSize), merged.size());
    }
}