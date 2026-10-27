package mk.finki.ukim.lost_and_found.config;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.domain.Professor; // Make sure this matches your entity package
import mk.finki.ukim.lost_and_found.model.enums.UserRole;
// import mk.finki.ukim.lost_and_found.model.enums.ProfessorTitle; // Import your enum if title is an enum
import mk.finki.ukim.lost_and_found.repository.UserRepository;
import mk.finki.ukim.lost_and_found.repository.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;

    public DataInitializer(UserRepository userRepository, ProfessorRepository professorRepository) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
    }

    // Uncomment when testing
    @Override
    public void run(String... args) {
//        // 1. Seed Student User
//        if (!userRepository.existsById("student")) {
//            User student = new User();
//            student.setId("student");
//            student.setName("Student");
//            student.setEmail("student@finki.ukim.mk");
//            student.setRole(UserRole.STUDENT);
//            userRepository.save(student);
//        }
//
//        // 2. Seed Professor Auth User
//        if (!userRepository.existsById("professor")) {
//            User prof = new User();
//            prof.setId("professor");
//            prof.setName("Professor");
//            prof.setEmail("professor@finki.ukim.mk");
//            prof.setRole(UserRole.PROFESSOR);
//            userRepository.save(prof);
//        }
//
//        // 3. Seed Corresponding Professor Profile Entity
//        if (!professorRepository.existsById("professor")) {
//            Professor professor = new Professor();
//            professor.setId("professor");
//            professor.setName("Professor");
//            professor.setEmail("professor@finki.ukim.mk");
//            professor.setActive(true);
//            professor.setOrderingRank((short) 1);
//
//            professorRepository.save(professor);
//        }
    }
}