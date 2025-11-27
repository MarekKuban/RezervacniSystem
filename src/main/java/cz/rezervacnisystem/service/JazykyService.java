package cz.rezervacnisystem.service;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Student;
import cz.rezervacnisystem.repository.JazykRepository;
import cz.rezervacnisystem.repository.RegistraceRepository;
import cz.rezervacnisystem.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JazykyService {

    private final StudentRepository studentRepository;
    private final JazykRepository jazykRepository;
    private final RegistraceRepository registraceRepository;

    public JazykyService(StudentRepository studentRepo, JazykRepository jazykRepo, RegistraceRepository registraceRepo) {
        this.studentRepository = studentRepo;
        this.jazykRepository = jazykRepo;
        this.registraceRepository = registraceRepo;
    }

    public Student prihlasitStudenta(String rodneCislo, String zadaneJmeno, String zadanePrijmeni) {
        // Normalizace RČ (odstranění mezer, doplnění lomítka)
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

    public List<Jazyk> ziskatVsechnyJazyky() {
        return jazykRepository.findAll();
    }

    public Registrace ziskatRegistraciStudenta(Student student) {
        List<Registrace> registrace = registraceRepository.findByStudent(student);
        return registrace.isEmpty() ? null : registrace.getFirst();
    }

    @Transactional
    public void vytvoritRegistraci(Integer studentId, Integer jazykId) throws Exception {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Student neexistuje"));

        Jazyk jazyk = jazykRepository.findById(jazykId)
                .orElseThrow(() -> new Exception("Jazyk neexistuje"));

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
}