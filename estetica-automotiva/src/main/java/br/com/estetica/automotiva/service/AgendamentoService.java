package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.Agendamento;
import br.com.estetica.automotiva.model.Cliente;
import br.com.estetica.automotiva.model.StatusAgendamento;
import br.com.estetica.automotiva.model.TipoServico;
import br.com.estetica.automotiva.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final TipoServicoService tipoServicoService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, TipoServicoService tipoServicoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.tipoServicoService = tipoServicoService;
    }

    public Agendamento agendar(Cliente cliente, String tipoServicoId, String placa, LocalDateTime inicio, String observacoes) {
        TipoServico servico = tipoServicoService.buscar(tipoServicoId);
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
        validarDisponibilidade(inicio, fim, null);
        Agendamento agendamento = new Agendamento();
        agendamento.setClienteId(cliente.getId());
        agendamento.setTipoServicoId(tipoServicoId);
        agendamento.setPlacaVeiculo(placa);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setObservacoes(observacoes);
        return agendamentoRepository.save(agendamento);
    }

    public Agendamento editar(String id, String tipoServicoId, String placa, LocalDateTime inicio, String observacoes) {
        Agendamento agendamento = buscar(id);
        TipoServico servico = tipoServicoService.buscar(tipoServicoId);
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
        validarDisponibilidade(inicio, fim, id);
        agendamento.setTipoServicoId(tipoServicoId);
        agendamento.setPlacaVeiculo(placa);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setObservacoes(observacoes);
        return agendamentoRepository.save(agendamento);
    }

    public void cancelar(String id) {
        Agendamento agendamento = buscar(id);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    public void validarDisponibilidade(LocalDateTime inicio, LocalDateTime fim, String ignorarId) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nao e permitido agendar servicos em data ou horario anterior ao atual");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("Horario final deve ser posterior ao inicial");
        }
        boolean conflito = agendamentoRepository
                .findByDataHoraInicioBeforeAndDataHoraFimAfterAndStatus(fim, inicio, StatusAgendamento.AGENDADO)
                .stream()
                .anyMatch(a -> ignorarId == null || !ignorarId.equals(a.getId()));
        if (conflito) {
            throw new IllegalArgumentException("Horario indisponivel: ja existe servico agendado neste periodo");
        }
    }

    public List<Agendamento> agendaDoDia(LocalDate data) {
        return agendamentoRepository.findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(data.atStartOfDay(), data.plusDays(1).atStartOfDay());
    }

    public List<Agendamento> agendaDaSemana(LocalDate dataInicial) {
        return agendamentoRepository.findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(dataInicial.atStartOfDay(), dataInicial.plusDays(7).atStartOfDay());
    }

    public List<Agendamento> proximosAgendamentos() {
        return agendamentoRepository.findTop20ByDataHoraInicioGreaterThanEqualAndStatusOrderByDataHoraInicioAsc(
                LocalDateTime.now(),
                StatusAgendamento.AGENDADO
        );
    }

    public List<Agendamento> porCliente(String clienteId) { return agendamentoRepository.findByClienteIdOrderByDataHoraInicioDesc(clienteId); }
    public Agendamento buscar(String id) { return agendamentoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Agendamento nao encontrado")); }
}
