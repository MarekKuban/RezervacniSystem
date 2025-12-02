package cz.rezervacnisystem.service;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Student;
import cz.rezervacnisystem.repository.JazykRepository;
import cz.rezervacnisystem.repository.RegistraceRepository;
import cz.rezervacnisystem.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JazykyService {

    private final StudentRepository studentRepository;
    private final JazykRepository jazykRepository;
    private final RegistraceRepository registraceRepository;

    // ČASOVÉ OMEZENÍ: Nastaveno od včerejška na 7 dní dopředu
    private static final LocalDateTime START_REGISTRACE = LocalDateTime.of(2025, 9, 1, 8, 0);
    private static final LocalDateTime KONEC_REGISTRACE = LocalDateTime.of(2025, 12, 2, 20, 19);

    public JazykyService(StudentRepository studentRepo, JazykRepository jazykRepo, RegistraceRepository registraceRepo) {
        this.studentRepository = studentRepo;
        this.jazykRepository = jazykRepo;
        this.registraceRepository = registraceRepo;
    }

    // --- NOVÁ METODA PRO KONTROLER (Bez ní by to spadlo) ---
    public List<Jazyk> ziskatJazykyProTridu(String trida) {
        return jazykRepository.findByTridaUrceni(trida);
    }

    public List<Jazyk> ziskatVsechnyJazyky() {
        return jazykRepository.findAll();
    }

    public Registrace ziskatRegistraciStudenta(Student student) {
        List<Registrace> registrace = registraceRepository.findByStudent(student);
        return registrace.isEmpty() ? null : registrace.get(0);
    }

    public Student prihlasitStudenta(String rodneCislo, String zadaneJmeno, String zadanePrijmeni) {
        // Ponechal jsem tvou logiku pro formátování RČ (to je super věc)
        String cisteRC = rodneCislo.replaceAll("[^0-9]", "");
        String formatovaneRC = rodneCislo;

        if (cisteRC.length() == 10) {
            formatovaneRC = cisteRC.substring(0, 6) + "/" + cisteRC.substring(6);
        }

        Student student = studentRepository.findByRodneCislo(formatovaneRC).orElse(null);

        if (student == null) {
            return null;
        }

        boolean jmenoSedi = student.getJmeno().trim().equalsIgnoreCase(zadaneJmeno.trim());
        boolean prijmeniSedi = student.getPrijmeni().trim().equalsIgnoreCase(zadanePrijmeni.trim());

        return (jmenoSedi && prijmeniSedi) ? student : null;
    }

    @Transactional
    public void vytvoritRegistraci(Integer studentId, Integer jazykId) throws Exception {
        // 1. KONTROLA ČASU (Nové)
        if (LocalDateTime.now().isBefore(START_REGISTRACE)) {
            throw new Exception("Registrace ještě nebyla spuštěna.");
        }
        if (LocalDateTime.now().isAfter(KONEC_REGISTRACE)) {
            throw new Exception("Registrace již byla ukončena.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Student neexistuje"));
        Jazyk jazyk = jazykRepository.findByIdWithLock(jazykId)
                .orElseThrow(() -> new Exception("Jazyk neexistuje"));

        // 2. KONTROLA TŘÍDY (Nové - aby si někdo z A nezapsal jazyk pro B)
        if (!jazyk.getTridaUrceni().equals(student.getTrida())) {
            throw new Exception("Tento jazyk není určen pro vaši třídu (" + student.getTrida() + ").");
        }

        if (!registraceRepository.findByStudent(student).isEmpty()) {
            throw new Exception("Už máte zvolený jazyk.");
        }

        if (jazyk.jePlno()) {
            throw new Exception("Kapacita jazyka je naplněna.");
        }

        Registrace novaRegistrace = new Registrace();
        novaRegistrace.setStudent(student);
        novaRegistrace.setJazyk(jazyk);
        registraceRepository.save(novaRegistrace);
    }

    public LocalDateTime getKonecRegistrace() {
        return KONEC_REGISTRACE;
    }

    @Transactional
    public void zrusitRegistraciStudenta(Student student) throws Exception {
        // I rušení je omezeno časem
        if (LocalDateTime.now().isAfter(KONEC_REGISTRACE)) {
            throw new Exception("Registrace byla ukončena, změny již nejsou možné.");
        }

        Registrace registrace = ziskatRegistraciStudenta(student);
        if (registrace != null) {
            registraceRepository.delete(registrace);
        } else {
            throw new Exception("Nemáte žádnou registraci ke zrušení.");
        }
    }

    @Transactional
    public void zrusitRegistraciAdminem(Integer registraceId) {
        registraceRepository.deleteById(registraceId);
    }
}