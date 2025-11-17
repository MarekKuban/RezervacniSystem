package cz.rezervacnisystem.controller;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Student;
import cz.rezervacnisystem.service.JazykyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JazykyController {

    private final JazykyService service;

    private static final String ADMIN_RC = "000000/0000";
    private static final String ADMIN_JMENO = "Admin";
    private static final String ADMIN_PRIJMENI = "Admin";

    public JazykyController(JazykyService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String jmeno,
            @RequestParam String prijmeni,
            @RequestParam String rodneCislo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. DETEKCE ADMINA (Přednostní kontrola)
        if (ADMIN_RC.equals(rodneCislo) &&
                ADMIN_JMENO.equalsIgnoreCase(jmeno) &&
                ADMIN_PRIJMENI.equalsIgnoreCase(prijmeni)) {

            // Uložíme do session značku, že je to admin
            session.setAttribute("adminLogged", true);
            session.setAttribute("userName", "Administrátor"); // Pro zobrazení v hlavičce

            return "redirect:/admin/dashboard"; // Přesměrujeme do velína
        }

        // 2. DETEKCE STUDENTA (Klasika)
        Student student = service.prihlasitStudenta(rodneCislo, jmeno, prijmeni);

        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Chyba přihlášení: Údaje nesouhlasí nebo student neexistuje.");
            return "redirect:/";
        }

        // Uložíme studenta
        session.setAttribute("prihlasenyStudent", student);
        return "redirect:/vyber";
    }


    @GetMapping("/vyber")
    public String showVyberJazyka(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("prihlasenyStudent");
        if (student == null) return "redirect:/";

        List<Jazyk> jazyky = service.ziskatVsechnyJazyky();
        Registrace existujiciVolba = service.ziskatRegistraciStudenta(student);

        model.addAttribute("student", student);
        model.addAttribute("jazyky", jazyky);
        model.addAttribute("mojeVolba", existujiciVolba);

        return "vyber";
    }

    @PostMapping("/zapis-jazyka")
    public String processZapis(@RequestParam Integer jazykId, HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("prihlasenyStudent");
        if (student == null) return "redirect:/";

        try {
            service.vytvoritRegistraci(student.getStudentId(), jazykId);
            redirectAttributes.addFlashAttribute("success", "Jazyk byl úspěšně zvolen!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vyber";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}