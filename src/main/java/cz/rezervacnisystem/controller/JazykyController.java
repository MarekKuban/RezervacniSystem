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
    private static final String ADMIN_RC = "000000/7350";
    private static final String ADMIN_JMENO = "Admin";
    private static final String ADMIN_PRIJMENI = "Admin";

    public JazykyController(JazykyService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String showLogin(Model model) {
        if (!model.containsAttribute("error")) model.addAttribute("error", null);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String jmeno, @RequestParam String prijmeni, @RequestParam String rodneCislo, HttpSession session, RedirectAttributes redirectAttributes) {
        if (ADMIN_RC.equals(rodneCislo) && ADMIN_JMENO.equalsIgnoreCase(jmeno) && ADMIN_PRIJMENI.equalsIgnoreCase(prijmeni)) {
            session.setAttribute("adminLogged", true);
            return "redirect:/admin/dashboard";
        }
        Student student = service.prihlasitStudenta(rodneCislo, jmeno, prijmeni);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Chyba přihlášení.");
            return "redirect:/";
        }
        session.setAttribute("prihlasenyStudent", student);
        return "redirect:/vyber";
    }

    @GetMapping("/vyber")
    public String showVyberJazyka(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("prihlasenyStudent");
        if (student == null) return "redirect:/";

        List<Jazyk> jazyky = service.ziskatJazykyProTridu(student.getTrida());
        Registrace existujiciVolba = service.ziskatRegistraciStudenta(student);

        model.addAttribute("student", student);
        model.addAttribute("jazyky", jazyky);
        model.addAttribute("mojeVolba", existujiciVolba);
        if (!model.containsAttribute("success")) model.addAttribute("success", null);
        if (!model.containsAttribute("error")) model.addAttribute("error", null);

        model.addAttribute("konecRegistrace", service.getKonecRegistrace());
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

    @PostMapping("/zrusit-jazyk")
    public String zrusitJazyk(HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("prihlasenyStudent");
        if (student == null) return "redirect:/";

        try {
            service.zrusitRegistraciStudenta(student);
            redirectAttributes.addFlashAttribute("success", "Volba zrušena.");
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