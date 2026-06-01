package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.Agendamento;
import br.com.estetica.automotiva.model.StatusAgendamento;
import br.com.estetica.automotiva.repository.AgendamentoRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgendamentoServiceTest {
    private final AgendamentoRepository agendamentoRepository = mock(AgendamentoRepository.class);
    private final TipoServicoService tipoServicoService = mock(TipoServicoService.class);
    private final AgendamentoService service = new AgendamentoService(agendamentoRepository, tipoServicoService);

    @Test
    void deveBloquearHorarioSobreposto() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        LocalDateTime fim = inicio.plusHours(1);
        Agendamento existente = new Agendamento();
        existente.setId("1");
        existente.setDataHoraInicio(inicio.minusMinutes(30));
        existente.setDataHoraFim(fim.minusMinutes(15));
        existente.setStatus(StatusAgendamento.AGENDADO);

        when(agendamentoRepository.findByDataHoraInicioBeforeAndDataHoraFimAfterAndStatus(fim, inicio, StatusAgendamento.AGENDADO))
                .thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class, () -> service.validarDisponibilidade(inicio, fim, null));
    }

    @Test
    void devePermitirEditarOProprioAgendamentoMesmoNoMesmoHorario() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        LocalDateTime fim = inicio.plusHours(1);
        Agendamento existente = new Agendamento();
        existente.setId("1");

        when(agendamentoRepository.findByDataHoraInicioBeforeAndDataHoraFimAfterAndStatus(fim, inicio, StatusAgendamento.AGENDADO))
                .thenReturn(List.of(existente));

        assertDoesNotThrow(() -> service.validarDisponibilidade(inicio, fim, "1"));
    }

    @Test
    void deveBloquearDataAnteriorAoMomentoAtual() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = inicio.plusHours(1);

        assertThrows(IllegalArgumentException.class, () -> service.validarDisponibilidade(inicio, fim, null));
    }
}
