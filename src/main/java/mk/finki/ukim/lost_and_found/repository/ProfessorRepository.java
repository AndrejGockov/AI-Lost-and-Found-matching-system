package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.Professor;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaSpecificationRepository<Professor, String> {

}
