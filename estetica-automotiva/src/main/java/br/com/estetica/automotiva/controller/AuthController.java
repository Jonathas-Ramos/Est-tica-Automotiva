package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.Cliente;
import br.com.estetica.automotiva.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final ClienteService clienteService;

    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute Cliente cliente,
                            BindingResult result,
                            @RequestParam String senha,
                            @RequestParam(name = "placasTexto", required = false) String placasTexto,
                            Model model) {
        if (result.hasErrors()) return "auth/cadastro";
        try {
            clienteService.cadastrarCliente(cliente, senha, placasTexto);
            return "redirect:/login?cadastro";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "auth/cadastro";
        }
    }
}
