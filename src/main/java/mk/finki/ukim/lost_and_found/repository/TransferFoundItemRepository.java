package mk.finki.ukim.lost_and_found.repository;

import jakarta.persistence.LockModeType;
import mk.finki.ukim.lost_and_found.model.domain.TransferFoundItem;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferFoundItemRepository extends JpaSpecificationRepository<TransferFoundItem, String> {

    @EntityGraph(attributePaths = "newHolder")
    List<TransferFoundItem> findByFoundItemIdOrderByDateSentAsc(String foundItemId);

    Optional<TransferFoundItem> findByFoundItem_IdAndAcceptedIsNull(String foundItemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TransferFoundItem t where t.id = :id and t.accepted is null")
    Optional<TransferFoundItem> findByIdWithLock(@Param("id") String id);
}