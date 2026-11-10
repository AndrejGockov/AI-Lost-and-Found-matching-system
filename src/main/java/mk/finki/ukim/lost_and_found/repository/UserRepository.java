package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface UserRepository extends JpaSpecificationRepository<User, String> {
    @Query("""
        select u from User u
        where u.active = true
          and u.id <> :excludeId
          and (lower(u.id) like concat('%', :q, '%')
            or lower(u.name) like concat('%', :q, '%')
            or lower(u.alias) like concat('%', :q, '%'))
        order by u.name, u.id
        """)
    List<User> searchActive(@Param("q") String lowerCaseQuery,
                            @Param("excludeId") String excludeId,
                            Pageable pageable);
}
