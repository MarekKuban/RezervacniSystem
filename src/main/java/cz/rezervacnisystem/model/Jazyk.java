package cz.rezervacnisystem.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "jazyky")
@Data // Lombok vygeneruje gettery, settery, toString...
public class Jazyk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jazyk_id")
    private Integer jazykId;

    @Column(name = "nazev_jazyka", nullable = false, unique = true)
    private String nazevJazyka;

    @Column(name = "max_kapacita", nullable = false)
    private Integer maxKapacita;

    // Zde je důležité: insertable=false, updatable=false
    // Důvod: Tuto hodnotu spravují tvé SQL TRIGGERY.
    // Java ji má jen pro čtení (aby viděla, jestli je plno).
    // Kdybychom to nechali zapisovat, Java by mohla přepsat výpočet triggeru.
    @Column(name = "aktualni_pocet_registrovanych", insertable = false, updatable = false)
    private Integer aktualniPocetRegistrovanych;

    // Pomocná metoda pro logiku
    public boolean jePlno() {
        return this.aktualniPocetRegistrovanych >= this.maxKapacita;
    }
}
