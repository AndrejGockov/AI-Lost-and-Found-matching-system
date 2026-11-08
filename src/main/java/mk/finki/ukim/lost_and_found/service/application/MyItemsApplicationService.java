package mk.finki.ukim.lost_and_found.service.application;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.enums.ItemType;
import org.springframework.data.domain.Page;

public interface MyItemsApplicationService {

    Page<DisplayItemResponseDto> findPage(User user, String title, ItemStatus status,
                                          ItemType type, int pageNum, int pageSize);
}