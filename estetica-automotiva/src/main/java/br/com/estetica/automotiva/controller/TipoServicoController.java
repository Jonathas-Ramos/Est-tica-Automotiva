package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.TipoServico;
import br.com.estetica.automotiva.service.TipoServicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/servicos")
public class TipoServicoController {
    private final TipoServicoService tipoServicoService;

    public TipoServicoController(TipoServicoService tipoServicoService) {
        this.tipoServicoService = tipoServicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicos", tipoServicoService.listarTodos());
        return "servicos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new TipoServico());
        return "servicos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("servico", tipoServicoService.buscar(id));
        model.addAttribute("editando", true);
        return "servicos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("servico") TipoServico servico, BindingResult result) {
        if (result.hasErrors()) return "servicos/form";
        tipoServicoService.salvar(servico);
        return "redirect:/servicos";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("servico") TipoServico servico,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            return "servicos/form";
        }
        servico.setId(id);
        tipoServicoService.salvar(servico);
        return "redirect:/servicos";
    }
}
