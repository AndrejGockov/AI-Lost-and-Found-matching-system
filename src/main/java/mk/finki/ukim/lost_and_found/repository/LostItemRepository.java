package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LostItemRepository extends JpaSpecificationRepository<LostItem, String> {
}
