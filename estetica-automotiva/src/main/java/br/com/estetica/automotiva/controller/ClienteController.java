package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.Cliente;
import br.com.estetica.automotiva.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/lista";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id) {
        clienteService.excluirDefinitivo(id);
        return "redirect:/clientes";
    }
}
