package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FoundItemService {

    List<FoundItem> findAll(ItemStatus status);

    List<FoundItem> findAllByUser(User user);

    Optional<FoundItem> findById(String id);

    FoundItem create(FoundItem foundItem);

    FoundItem update(String id, FoundItem foundItem);

    FoundItem updateStatus(String id, ItemStatus itemStatus);

    FoundItem updateHolder(String foundItemId, String newHolderId);

    Optional<FoundItem> deleteById(String id);

    // Students filtering. identifiable selects which of the two found-items
    // lists this page belongs to: true -> full/unrestricted list,
    // false -> restricted list.
    Page<FoundItem> findPage(String title, boolean identifiable, int pageNum, int pageSize);

    // Employees filtering
    Page<FoundItem> findPage(String title, String posterName, ItemStatus status, boolean identifiable, int pageNum, int pageSize);
}