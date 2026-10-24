package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaSpecificationRepository<LostItem, String> {
    @Override
    @EntityGraph(attributePaths = "poster")
    List<LostItem> findAll();

    @EntityGraph(attributePaths = "poster")
    List<LostItem> findAllByStatus(ItemStatus status);

    @EntityGraph(attributePaths = "poster")
    List<LostItem> findAllByPoster(User poster);
}
