package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaSpecificationRepository<User, String> {

}
