package cz.rezervacnisystem.repository;

import cz.rezervacnisystem.model.Uzivatel; // Import entity z hlavního balíčku
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UzivatelRepository extends JpaRepository<Uzivatel, Integer> {
    Optional<Uzivatel> findByRodneCislo(String rodneCislo);
}
