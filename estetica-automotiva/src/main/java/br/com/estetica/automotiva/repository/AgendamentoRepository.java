package br.com.estetica.automotiva.repository;

import br.com.estetica.automotiva.model.Agendamento;
import br.com.estetica.automotiva.model.StatusAgendamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
    List<Agendamento> findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(LocalDateTime inicio, LocalDateTime fim);
    List<Agendamento> findByClienteIdOrderByDataHoraInicioDesc(String clienteId);
    List<Agendamento> findByDataHoraInicioBeforeAndDataHoraFimAfterAndStatus(LocalDateTime fim, LocalDateTime inicio, StatusAgendamento status);
    List<Agendamento> findTop20ByDataHoraInicioGreaterThanEqualAndStatusOrderByDataHoraInicioAsc(LocalDateTime agora, StatusAgendamento status);
}
