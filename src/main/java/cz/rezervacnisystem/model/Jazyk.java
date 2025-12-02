package cz.rezervacnisystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "jazyky")
@Data
public class Jazyk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jazyk_id")
    private Integer jazykId;

    @Column(name = "nazev_jazyka", nullable = false)
    private String nazevJazyka;

    @Column(name = "trida_urceni", nullable = false)
    private String tridaUrceni;

    @Column(name = "max_kapacita", nullable = false)
    private Integer maxKapacita;

    @Column(name = "aktualni_pocet_registrovanych", insertable = false, updatable = false)
    private Integer aktualniPocetRegistrovanych;

    @OneToMany(mappedBy = "jazyk")
    @ToString.Exclude
    private List<Registrace> registrace;

    public boolean jePlno() {
        int aktualni = (aktualniPocetRegistrovanych == null) ? 0 : aktualniPocetRegistrovanych;
        return aktualni >= maxKapacita;
    }
}