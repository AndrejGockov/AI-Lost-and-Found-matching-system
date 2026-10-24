package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.LostItemNotFoundException;
import mk.finki.ukim.lost_and_found.repository.LostItemRepository;
import mk.finki.ukim.lost_and_found.service.domain.LostItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static mk.finki.ukim.lost_and_found.service.specifications.FieldFilterSpecification.*;

@Service
@AllArgsConstructor
public class LostItemServiceImpl implements LostItemService {
    private final LostItemRepository lostItemRepository;

    @Override
    public List<LostItem> findAll(ItemStatus status) {
        return status == null
                ? lostItemRepository.findAll()
                : lostItemRepository.findAllByStatus(status);
    }

    @Override
    public List<LostItem> findAllByUser(User user) {
        return lostItemRepository.findAllByPoster(user);
    }

    @Override
    public Optional<LostItem> findById(String id) {
        return lostItemRepository.findById(id);
    }

    @Override
    public LostItem create(LostItem lostItem) {
        return lostItemRepository.save(lostItem);
    }

    @Override
    @Transactional
    public LostItem update(String id, LostItem lostItem) {
        LostItem existing = lostItemRepository.findById(id)
                .orElseThrow(() -> new LostItemNotFoundException(id));

        existing.setTitle(lostItem.getTitle());
        existing.setDescription(lostItem.getDescription());
        existing.setDateLost(lostItem.getDateLost());
        existing.setLocationLost(lostItem.getLocationLost());

        // Sets flag to true and nullifies old embeddings
        existing.setUpdated(true);
        existing.setEmbedding(null);

        // New images come from the request, re-attach them to the managed item
        lostItem.getImages().forEach(image -> {
            image.setItem(existing);
            existing.getImages().add(image);
        });

        return lostItemRepository.save(existing);
    }

    @Override
    @Transactional
    public LostItem updateStatus(String id, ItemStatus itemStatus) {
        LostItem existing = lostItemRepository.findById(id)
                .orElseThrow(() -> new LostItemNotFoundException(id));
        existing.setStatus(itemStatus);
        return lostItemRepository.save(existing);
    }

    @Override
    public Optional<LostItem> deleteById(String id) {
        Optional<LostItem> lostItem = findById(id);
        lostItem.ifPresent(lostItemRepository::delete);
        return lostItem;
    }

    // Students filtering
    @Override
    public Page<LostItem> findPage(String title, int pageNum, int pageSize) {
        Specification<LostItem> specification = Specification.allOf(
                filterEqualsV(LostItem.class, "status", ItemStatus.UNRESOLVED),
                filterContainsText(LostItem.class, "title", title)
        );

        return lostItemRepository.findAll(specification, PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "datePosted")));
    }

    // Employees filtering
    @Override
    public Page<LostItem> findPage(String title, String posterName, ItemStatus status, int pageNum, int pageSize) {
        Specification<LostItem> specification = Specification.allOf(
                filterEqualsV(LostItem.class, "status", status),
                filterContainsText(LostItem.class, "title", title),
                filterContainsText(LostItem.class, "poster.name", posterName)
        );

        return lostItemRepository.findAll(specification, PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "datePosted")));
    }
}