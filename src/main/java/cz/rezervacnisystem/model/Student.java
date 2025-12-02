package cz.rezervacnisystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "studenti")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "rodne_cislo", nullable = false, unique = true)
    private String rodneCislo;

    @Column(nullable = false)
    private String jmeno;

    @Column(nullable = false)
    private String prijmeni;

    @Column(nullable = false)
    private String trida;
}