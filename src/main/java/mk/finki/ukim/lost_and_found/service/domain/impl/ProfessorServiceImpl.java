package mk.finki.ukim.lost_and_found.service.domain.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.Professor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ProfessorTitle;
import mk.finki.ukim.lost_and_found.model.exceptions.ProfessorNotFoundException;
import mk.finki.ukim.lost_and_found.repository.ProfessorRepository;
import mk.finki.ukim.lost_and_found.repository.UserRepository;
import mk.finki.ukim.lost_and_found.service.domain.ProfessorService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static mk.finki.ukim.lost_and_found.service.specifications.FieldFilterSpecification.filterContainsText;

@Service
@AllArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;

    @Override
    public Professor getProfessorById(String id) throws ProfessorNotFoundException {
        // Try direct professor lookup first
        var professor = professorRepository.findById(id);
        if (professor.isPresent()) {
            return professor.get();
        }

        // Fallback: search for user by id or alias
//        var user = userRepository.findByIdOrAlias(id, id);
//        if (user.isPresent()) {
//            User foundUser = user.get();
//            // Verify user has professor role
//            if (foundUser.getRole().isProfessor()) {
//                // Try to load professor using the resolved user id
//                var resolvedProfessor = professorRepository.findById(foundUser.getId());
//                if (resolvedProfessor.isPresent()) {
//                    return resolvedProfessor.get();
//                }
//            }
//        }

        // If nothing found, throw exception
        throw new ProfessorNotFoundException("Professor with id " + id + " doesn't exist");
    }

    @Override
    public List<Professor> listAllProfessors(String professorName) {
        Specification<Professor> spec = Specification.where(null);
        if(professorName!=null && !professorName.isEmpty()){
            Specification<Professor> nameSpec = filterContainsText(Professor.class, "name", professorName);
            Specification<Professor> idSpec = filterContainsText(Professor.class, "id", professorName);

            spec = spec.and(nameSpec.or(idSpec));
        }

        Specification<Professor> excludeSpec = (root, query, criteriaBuilder) -> {
            Predicate notDemonstrator = criteriaBuilder.notLike(root.get("name"), "%Демонстратор%");
            Predicate notIndustry = criteriaBuilder.notLike(root.get("name"), "%Индустрија%");
            Predicate notRetired = criteriaBuilder.notEqual(root.get("title"), ProfessorTitle.RETIRED);
            return criteriaBuilder.and(notDemonstrator, notIndustry, notRetired);
        };

        Specification<Professor> excludeInactiveSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));

        spec = spec.and(excludeSpec).and(excludeInactiveSpec);

        return null;
//        return professorRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public TreeMap<Character, List<Professor>> findAllProfessorsSortedByFirstName(String professorName) {
        List<Character> macedonianAlphabet = Arrays.asList(
                'А', 'Б', 'В', 'Г', 'Д', 'Ѓ', 'Е', 'Ж', 'З', 'Ѕ', 'И', 'Ј',
                'К', 'Л', 'Љ', 'М', 'Н', 'Њ', 'О', 'П', 'Р', 'С', 'Т', 'Ќ',
                'У', 'Ф', 'Х', 'Ц', 'Ч', 'Џ', 'Ш'
        );

        TreeMap<Character, List<Professor>> professorsMap = new TreeMap<>((char1, char2) -> {
            int index1 = macedonianAlphabet.indexOf(char1);
            int index2 = macedonianAlphabet.indexOf(char2);
            return Integer.compare(index1, index2);
        });

        listAllProfessors(professorName).forEach(p ->
        {
            char character = p.getName().toUpperCase().charAt(0);
            if (macedonianAlphabet.contains(character)) {
                List<Professor> professors = professorsMap.getOrDefault(character, new ArrayList<>());
                professors.add(p);
                professorsMap.put(character, professors);
            }
        });
        return professorsMap;
    }
}
