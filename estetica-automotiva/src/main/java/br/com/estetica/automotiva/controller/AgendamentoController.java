package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.Agendamento;
import br.com.estetica.automotiva.model.Cliente;
import br.com.estetica.automotiva.model.TipoServico;
import br.com.estetica.automotiva.service.AgendamentoService;
import br.com.estetica.automotiva.service.ClienteService;
import br.com.estetica.automotiva.service.TipoServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class AgendamentoController {
    private final AgendamentoService agendamentoService;
    private final ClienteService clienteService;
    private final TipoServicoService tipoServicoService;

    public AgendamentoController(AgendamentoService agendamentoService, ClienteService clienteService, TipoServicoService tipoServicoService) {
        this.agendamentoService = agendamentoService;
        this.clienteService = clienteService;
        this.tipoServicoService = tipoServicoService;
    }

    @GetMapping("/agendamentos")
    public String meusAgendamentos(Principal principal, Model model) {
        if (principalGestor(principal)) {
            return "redirect:/gestor/agenda";
        }
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        model.addAttribute("cliente", cliente);
        model.addAttribute("agendamentos", agendamentoService.porCliente(cliente.getId()));
        model.addAttribute("servicosPorId", servicosPorId());
        return "agendamentos/lista";
    }

    @GetMapping("/agendamentos/novo")
    public String novo(Principal principal, Model model) {
        if (principalGestor(principal)) {
            return "redirect:/gestor/agenda";
        }
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        model.addAttribute("cliente", cliente);
        model.addAttribute("servicos", tipoServicoService.listarAtivos());
        model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
        return "agendamentos/form";
    }

    @PostMapping("/agendamentos")
    public String agendar(Principal principal,
                          @RequestParam String tipoServicoId,
                          @RequestParam String placaVeiculo,
                          @RequestParam String dataHoraInicio,
                          @RequestParam(required = false) String observacoes,
                          Model model) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        try {
            agendamentoService.agendar(cliente, tipoServicoId, placaVeiculo, LocalDateTime.parse(dataHoraInicio), observacoes);
            return "redirect:/agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("servicos", tipoServicoService.listarAtivos());
            model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
            return "agendamentos/form";
        }
    }

    @GetMapping("/agendamentos/{id}/editar")
    public String editar(@PathVariable String id, Principal principal, Model model) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        Agendamento agendamento = agendamentoDoCliente(id, cliente);
        model.addAttribute("cliente", cliente);
        model.addAttribute("agendamento", agendamento);
        model.addAttribute("servicos", tipoServicoService.listarAtivos());
        model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
        return "agendamentos/form";
    }

    @PostMapping("/agendamentos/{id}/editar")
    public String atualizar(@PathVariable String id,
                            @RequestParam String tipoServicoId,
                            @RequestParam String placaVeiculo,
                            @RequestParam String dataHoraInicio,
                            @RequestParam(required = false) String observacoes,
                            Model model,
                            Principal principal) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        agendamentoDoCliente(id, cliente);
        try {
            agendamentoService.editar(id, tipoServicoId, placaVeiculo, LocalDateTime.parse(dataHoraInicio), observacoes);
            return "redirect:/agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("agendamento", agendamentoService.buscar(id));
            model.addAttribute("servicos", tipoServicoService.listarAtivos());
            model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
            return "agendamentos/form";
        }
    }

    @PostMapping("/agendamentos/{id}/cancelar")
    public String cancelarCliente(@PathVariable String id, Principal principal) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        agendamentoDoCliente(id, cliente);
        agendamentoService.cancelar(id);
        return "redirect:/agendamentos";
    }

    @PostMapping("/gestor/agendamentos/{id}/cancelar")
    public String cancelarGestor(@PathVariable String id) {
        agendamentoService.cancelar(id);
        return "redirect:/gestor/agenda";
    }

    @GetMapping("/gestor/agenda")
    public String agendaGestor(@RequestParam(defaultValue = "dia") String visao,
                               @RequestParam(required = false) String data,
                               @RequestParam(required = false) String tipoServicoId,
                               Model model) {
        LocalDate referencia = data == null || data.isBlank() ? LocalDate.now() : LocalDate.parse(data);
        List<Agendamento> agendamentos = switch (visao) {
            case "semana" -> agendamentoService.agendaDaSemana(referencia);
            case "proximos" -> agendamentoService.proximosAgendamentos();
            default -> agendamentoService.agendaDoDia(referencia);
        };
        if (tipoServicoId != null && !tipoServicoId.isBlank()) {
            agendamentos = agendamentos.stream()
                    .filter(ag -> tipoServicoId.equals(ag.getTipoServicoId()))
                    .toList();
        }
        model.addAttribute("visao", visao);
        model.addAttribute("data", referencia);
        model.addAttribute("tipoServicoId", tipoServicoId);
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("servicos", tipoServicoService.listarAtivos());
        model.addAttribute("servicosPorId", servicosPorId());
        model.addAttribute("clientesPorId", clienteService.listarTodos().stream().collect(Collectors.toMap(Cliente::getId, Function.identity())));
        return "agendamentos/agenda-gestor";
    }

    @GetMapping("/gestor/agendamentos/{id}/editar")
    public String editarGestor(@PathVariable String id, Model model) {
        Agendamento agendamento = agendamentoService.buscar(id);
        Cliente cliente = clienteService.buscarPorId(agendamento.getClienteId());
        model.addAttribute("cliente", cliente);
        model.addAttribute("agendamento", agendamento);
        model.addAttribute("servicos", tipoServicoService.listarAtivos());
        model.addAttribute("gestor", true);
        model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
        return "agendamentos/form";
    }

    @PostMapping("/gestor/agendamentos/{id}/editar")
    public String atualizarGestor(@PathVariable String id,
                                  @RequestParam String tipoServicoId,
                                  @RequestParam String placaVeiculo,
                                  @RequestParam String dataHoraInicio,
                                  @RequestParam(required = false) String observacoes,
                                  Model model) {
        try {
            agendamentoService.editar(id, tipoServicoId, placaVeiculo, LocalDateTime.parse(dataHoraInicio), observacoes);
            return "redirect:/gestor/agenda";
        } catch (IllegalArgumentException ex) {
            Agendamento agendamento = agendamentoService.buscar(id);
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("cliente", clienteService.buscarPorId(agendamento.getClienteId()));
            model.addAttribute("agendamento", agendamento);
            model.addAttribute("servicos", tipoServicoService.listarAtivos());
            model.addAttribute("gestor", true);
            model.addAttribute("dataHoraMin", LocalDateTime.now().withSecond(0).withNano(0));
            return "agendamentos/form";
        }
    }

    private Map<String, TipoServico> servicosPorId() {
        return tipoServicoService.listarTodos().stream().collect(Collectors.toMap(TipoServico::getId, Function.identity()));
    }

    private boolean principalGestor(Principal principal) {
        return principal instanceof org.springframework.security.core.Authentication auth
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
    }

    private Agendamento agendamentoDoCliente(String id, Cliente cliente) {
        Agendamento agendamento = agendamentoService.buscar(id);
        if (!cliente.getId().equals(agendamento.getClienteId())) {
            throw new IllegalArgumentException("Agendamento nao pertence ao cliente autenticado");
        }
        return agendamento;
    }
}
