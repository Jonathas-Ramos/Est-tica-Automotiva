package br.com.estetica.automotiva.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Authentication authentication) {
        boolean gestor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        return gestor ? "redirect:/gestor/agenda" : "redirect:/agendamentos";
    }
}
