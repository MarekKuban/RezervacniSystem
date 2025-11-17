package cz.rezervacnisystem.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrace")
@Data
public class Registrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registrace_id")
    private Integer registraceId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "jazyk_id", nullable = false)
    private Jazyk jazyk;

    @Column(name = "datum_registrace", insertable = false, updatable = false)
    private LocalDateTime datumRegistrace;
}
