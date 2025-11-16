package cz.rezervacnisystem.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "uzivatele")
@Data
public class Uzivatel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uzivatel_id")
    private Integer uzivatelId;

    @Column(name = "rodne_cislo", nullable = false, unique = true)
    private String rodneCislo;

    @Column(nullable = false)
    private String jmeno;

    @Column(nullable = false)
    private String prijmeni;

    @Column(name = "datum_registrace", insertable = false, updatable = false)
    private LocalDateTime datumRegistrace;
}