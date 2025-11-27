package cz.rezervacnisystem.controller;

import cz.rezervacnisystem.model.Jazyk;
import cz.rezervacnisystem.service.JazykyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final JazykyService service;

    public AdminController(JazykyService service) {
        this.service = service;
    }


    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Kontrola: Je v session značka "adminLogged"?
        if (session.getAttribute("adminLogged") == null) {
            // Pokud ne, vykopneme ho na hlavní login
            return "redirect:/";
        }

        List<Jazyk> jazyky = service.ziskatVsechnyJazyky();
        model.addAttribute("jazyky", jazyky);

        return "admin-dashboard";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}