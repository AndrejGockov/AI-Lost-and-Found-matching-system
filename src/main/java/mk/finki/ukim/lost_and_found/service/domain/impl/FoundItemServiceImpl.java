package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.FoundItemNotFoundException;
import mk.finki.ukim.lost_and_found.repository.FoundItemRepository;
import mk.finki.ukim.lost_and_found.service.domain.FoundItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static mk.finki.ukim.lost_and_found.service.specifications.FieldFilterSpecification.filterContainsText;
import static mk.finki.ukim.lost_and_found.service.specifications.FieldFilterSpecification.filterEqualsV;

@Service
@AllArgsConstructor
public class FoundItemServiceImpl implements FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final UserService userService;

    @Override
    public List<FoundItem> findAll(ItemStatus status) {
        if (status == null) {
            return foundItemRepository.findAll();
        }
        return foundItemRepository.findAllByStatus(status);
    }

    // Refers to the current holder, not poster
    @Override
    public List<FoundItem> findAllByUser(User user) {
        return foundItemRepository.findAllByHolder(user);
    }

    @Override
    public Optional<FoundItem> findById(String id) {
        return foundItemRepository.findById(id);
    }

    @Override
    public FoundItem create(FoundItem foundItem) {
        return foundItemRepository.save(foundItem);
    }

    @Override
    public FoundItem update(String id, FoundItem foundItem) {
        FoundItem existing = foundItemRepository.findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        existing.setTitle(foundItem.getTitle());
        existing.setDescription(foundItem.getDescription());
        existing.setDateFound(foundItem.getDateFound());
        existing.setLocationFound(foundItem.getLocationFound());
        existing.setIdentifiable(foundItem.isIdentifiable());

        // Sets flag to true and nullifies old embeddings
        existing.setUpdated(true);
        existing.setEmbedding(null);

        // 1. Remove only the images checked for deletion
        if (foundItem.getDeleteImageIds() != null && !foundItem.getDeleteImageIds().isEmpty()) {
            existing.getImages().removeIf(img -> foundItem.getDeleteImageIds().contains(img.getId()));
        }

        // 2. Append newly uploaded images to the existing collection
        List<FoundItemImage> newImages = foundItem.getImages();
        if (newImages != null && !newImages.isEmpty()) {
            for (FoundItemImage image : newImages) {
                image.setItem(existing);
                existing.getImages().add(image);
            }
        }

        return foundItemRepository.save(existing);
    }

    @Override
    public FoundItem updateStatus(String id, ItemStatus itemStatus) {
        FoundItem existing = foundItemRepository.findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        existing.setStatus(itemStatus);

        return foundItemRepository.save(existing);
    }

    @Override
    public FoundItem updateHolder(String foundItemId, String newHolderId) {
        FoundItem existing = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new FoundItemNotFoundException(foundItemId));

        User newHolder = userService.findByUsername(newHolderId);
        existing.setHolder(newHolder);

        return foundItemRepository.save(existing);
    }

    @Override
    public Optional<FoundItem> deleteById(String id) {
        Optional<FoundItem> existing = foundItemRepository.findById(id);
        existing.ifPresent(item -> foundItemRepository.deleteById(id));
        return existing;
    }

    // Students filtering. identifiable selects which of the two found-items
    // lists this page belongs to: true -> full/unrestricted list,
    // false -> restricted list.
    @Override
    public Page<FoundItem> findPage(String title, boolean identifiable, int pageNum, int pageSize) {
        Specification<FoundItem> specification = Specification.allOf(
                filterEqualsV(FoundItem.class, "status", ItemStatus.UNRESOLVED),
                filterEqualsV(FoundItem.class, "identifiable", identifiable),
                filterContainsText(FoundItem.class, "title", title)
        );

        return foundItemRepository.findAll(specification, PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "datePosted")));
    }

    // Employees filtering
    @Override
    public Page<FoundItem> findPage(String title, String posterName, ItemStatus status, boolean identifiable, int pageNum, int pageSize) {
        Specification<FoundItem> specification = Specification.allOf(
                filterEqualsV(FoundItem.class, "status", status),
                filterEqualsV(FoundItem.class, "identifiable", identifiable),
                filterContainsText(FoundItem.class, "title", title),
                filterContainsText(FoundItem.class, "poster.name", posterName)
        );

        return foundItemRepository.findAll(specification, PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "datePosted")));
    }
}