package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.Professor;

import java.util.List;
import java.util.TreeMap;


public interface ProfessorService {

    Professor getProfessorById(String id);

    List<Professor> listAllProfessors(String professorName);

    TreeMap<Character, List<Professor>> findAllProfessorsSortedByFirstName(String professorName);
}
