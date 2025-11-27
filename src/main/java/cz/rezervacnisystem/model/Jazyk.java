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

    @Column(name = "nazev_jazyka", nullable = false, unique = true)
    private String nazevJazyka;

    @Column(name = "max_kapacita", nullable = false)
    private Integer maxKapacita;

    @Column(name = "aktualni_pocet_registrovanych", insertable = false, updatable = false)
    private Integer aktualniPocetRegistrovanych;

    // Vazba pro Admin panel: Umožní nám získat seznam studentů pro tento jazyk
    @OneToMany(mappedBy = "jazyk")
    @ToString.Exclude // Důležité pro Lombok, aby se nezacyklil
    private List<Registrace> registrace;

    public boolean jePlno() {
        // Ošetření null hodnoty pro bezpečnost
        int aktualni = (aktualniPocetRegistrovanych == null) ? 0 : aktualniPocetRegistrovanych;
        return aktualni >= maxKapacita;
    }
}