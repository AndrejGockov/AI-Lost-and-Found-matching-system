package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoundItemRepository extends JpaSpecificationRepository<FoundItem, String> {
}
