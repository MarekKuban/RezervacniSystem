package cz.rezervacnisystem.service;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Uzivatel;
import cz.rezervacnisystem.repository.JazykRepository;
import cz.rezervacnisystem.repository.RegistraceRepository;
import cz.rezervacnisystem.repository.UzivatelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JazykyService {

    private final UzivatelRepository uzivatelRepository;
    private final JazykRepository jazykRepository;
    private final RegistraceRepository registraceRepository;

    public JazykyService(UzivatelRepository uzivatelRepo, JazykRepository jazykRepo, RegistraceRepository registraceRepo) {
        this.uzivatelRepository = uzivatelRepo;
        this.jazykRepository = jazykRepo;
        this.registraceRepository = registraceRepo;
    }

    // 1. Přihlášení - vrací objekt Uzivatel
    // Upravená metoda přijímá 3 parametry
    public Uzivatel prihlasitUzivatele(String rodneCislo, String zadaneJmeno, String zadanePrijmeni) {
        // 1. Najdeme uživatele podle RČ (to je unikátní klíč)
        Uzivatel uzivatel = uzivatelRepository.findByRodneCislo(rodneCislo).orElse(null);

        // 2. Pokud uživatel neexistuje, vrátíme null
        if (uzivatel == null) {
            return null;
        }

        // 3. Pokud existuje, zkontrolujeme Jméno a Příjmení
        // (Ignorujeme velikost písmen a mezery na začátku/konci)
        boolean jmenoSedi = uzivatel.getJmeno().trim().equalsIgnoreCase(zadaneJmeno.trim());
        boolean prijmeniSedi = uzivatel.getPrijmeni().trim().equalsIgnoreCase(zadanePrijmeni.trim());

        if (jmenoSedi && prijmeniSedi) {
            return uzivatel; // Všechno sedí, pouštíme ho
        } else {
            return null; // RČ sice sedí, ale jméno ne -> nepustíme ho (bezpečnost)
        }
    }

    // 2. Seznam jazyků
    public List<Jazyk> ziskatVsechnyJazyky() {
        return jazykRepository.findAll();
    }

    // 3. Kontrola existující registrace - přijímá objekt Uzivatel
    public Registrace ziskatRegistraciUzivatele(Uzivatel uzivatel) {
        List<Registrace> registrace = registraceRepository.findByUzivatel(uzivatel);
        return registrace.isEmpty() ? null : registrace.get(0);
    }

    // 4. Vytvoření registrace
    @Transactional
    public void vytvoritRegistraci(Integer uzivatelId, Integer jazykId) throws Exception {
        // Načítáme entitu Uzivatel
        Uzivatel uzivatel = uzivatelRepository.findById(uzivatelId)
                .orElseThrow(() -> new Exception("Uživatel neexistuje"));

        Jazyk jazyk = jazykRepository.findById(jazykId)
                .orElseThrow(() -> new Exception("Jazyk neexistuje"));

        // Kontrola duplicity
        if (!registraceRepository.findByUzivatel(uzivatel).isEmpty()) {
            throw new Exception("Už jste zapsán(a) na jiný kurz.");
        }

        // Kontrola kapacity
        if (jazyk.jePlno()) {
            throw new Exception("Kapacita kurzu je naplněna.");
        }

        Registrace novaRegistrace = new Registrace();
        novaRegistrace.setUzivatel(uzivatel);
        novaRegistrace.setJazyk(jazyk);
        registraceRepository.save(novaRegistrace);
    }
}