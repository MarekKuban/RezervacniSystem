package cz.rezervacnisystem.repository;

import cz.rezervacnisystem.model.Jazyk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JazykRepository extends JpaRepository<Jazyk, Integer> {
}