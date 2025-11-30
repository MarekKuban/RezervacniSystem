package cz.rezervacnisystem.repository;

import cz.rezervacnisystem.model.Jazyk;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JazykRepository extends JpaRepository<Jazyk, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Jazyk j WHERE j.jazykId = :id")
    Optional<Jazyk> findByIdWithLock(@Param("id") Integer id);
}