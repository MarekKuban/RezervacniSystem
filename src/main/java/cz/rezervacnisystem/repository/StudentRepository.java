package cz.rezervacnisystem.repository;

import cz.rezervacnisystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByRodneCislo(String rodneCislo);
}
