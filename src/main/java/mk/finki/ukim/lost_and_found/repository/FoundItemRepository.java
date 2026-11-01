package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaSpecificationRepository<FoundItem, String> {
    @Override
    @EntityGraph(attributePaths = {"poster", "holder"})
    List<FoundItem> findAll();

    @EntityGraph(attributePaths = {"poster", "holder"})
    List<FoundItem> findAllByStatus(ItemStatus status);

    @EntityGraph(attributePaths = {"poster", "holder"})
    List<FoundItem> findAllByHolder(User holder);

}
