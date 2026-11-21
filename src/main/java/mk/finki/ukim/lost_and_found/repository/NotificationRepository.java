package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.Notification;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaSpecificationRepository<Notification, String> {
    @EntityGraph(attributePaths = "user")
    List<Notification> findAllByUserOrderByDateCreatedDesc(User user);

    @EntityGraph(attributePaths = "user")
    List<Notification> findAllByUserAndViewedFalse(User user);
}
