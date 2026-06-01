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

import java.security.Principal;

@Controller
public class MinhaContaController {
    private final ClienteService clienteService;

    public MinhaContaController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/minha-conta")
    public String minhaConta(Principal principal, Model model) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        model.addAttribute("cliente", cliente);
        model.addAttribute("placasTexto", String.join(", ", cliente.getPlacas()));
        return "clientes/minha-conta";
    }

    @PostMapping("/minha-conta")
    public String atualizarMinhaConta(Principal principal,
                                      @Valid @ModelAttribute Cliente cliente,
                                      BindingResult result,
                                      @RequestParam(name = "placasTexto", required = false) String placasTexto,
                                      Model model) {
        Cliente autenticado = clienteService.buscarPorEmail(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("placasTexto", placasTexto);
            return "clientes/minha-conta";
        }
        clienteService.atualizarCliente(autenticado.getId(), cliente, placasTexto);
        return "redirect:/minha-conta?sucesso";
    }
}
