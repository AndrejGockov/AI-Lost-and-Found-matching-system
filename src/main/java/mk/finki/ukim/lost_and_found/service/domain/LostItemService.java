package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LostItemService {
    List<LostItem> findAll(ItemStatus status);

    List<LostItem> findAllByUser(User user);

    Optional<LostItem> findById(String id);

    LostItem create(LostItem lostItem);

    LostItem update(String id, LostItem lostItem);

    LostItem updateStatus(String id, ItemStatus itemStatus);

    Optional<LostItem> deleteById(String id);


    Page<LostItem> findPage(String title, int pageNum, int pageSize);

    Page<LostItem> findPage(String title, String posterName, ItemStatus status, int pageNum, int pageSize);
}
