package cz.rezervacnisystem.repository;

import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Uzivatel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistraceRepository extends JpaRepository<Registrace, Integer> {
    List<Registrace> findByUzivatel(Uzivatel uzivatel);
}