package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;

public interface TransferFoundItemRepository extends JpaSpecificationRepository<TransferFoundItem, String> {
}