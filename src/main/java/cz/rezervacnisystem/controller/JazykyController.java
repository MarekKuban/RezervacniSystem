package cz.rezervacnisystem.controller;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.model.Registrace;
import cz.rezervacnisystem.model.Uzivatel;
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

    public JazykyController(JazykyService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String jmeno,      // <--- Nové
            @RequestParam String prijmeni,   // <--- Nové
            @RequestParam String rodneCislo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Posíláme všechno do Service na ověření
        Uzivatel uzivatel = service.prihlasitUzivatele(rodneCislo, jmeno, prijmeni);

        if (uzivatel == null) {
            // Upravená chybová hláška
            redirectAttributes.addFlashAttribute("error", "Chyba přihlášení: Údaje nesouhlasí nebo uživatel neexistuje.");
            return "redirect:/";
        }

        session.setAttribute("prihlasenyUzivatel", uzivatel);
        return "redirect:/vyber";
    }

    // Hlavní stránka s výběrem jazyků
    @GetMapping("/vyber")
    public String showVyberJazyka(HttpSession session, Model model) {
        Uzivatel uzivatel = (Uzivatel) session.getAttribute("prihlasenyUzivatel");

        if (uzivatel == null) {
            return "redirect:/";
        }

        List<Jazyk> jazyky = service.ziskatVsechnyJazyky();
        Registrace existujiciVolba = service.ziskatRegistraciUzivatele(uzivatel);

        model.addAttribute("uzivatel", uzivatel);
        model.addAttribute("jazyky", jazyky);
        model.addAttribute("mojeVolba", existujiciVolba); // Přejmenováno z mojeRegistrace

        return "vyber"; // Odkazuje na soubor vyber.html (původně kurzy.html)
    }

    @PostMapping("/zapis-jazyka") // Změna URL akce
    public String processZapis(@RequestParam Integer jazykId, HttpSession session, RedirectAttributes redirectAttributes) {
        Uzivatel uzivatel = (Uzivatel) session.getAttribute("prihlasenyUzivatel");

        if (uzivatel == null) return "redirect:/";

        try {
            service.vytvoritRegistraci(uzivatel.getUzivatelId(), jazykId);
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