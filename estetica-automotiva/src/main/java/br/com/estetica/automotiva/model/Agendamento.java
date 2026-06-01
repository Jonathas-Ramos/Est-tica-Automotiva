package br.com.estetica.automotiva.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "agendamentos")
public class Agendamento {
    @Id
    private String id;

    @NotBlank
    private String clienteId;

    @NotBlank
    private String tipoServicoId;

    @NotBlank
    private String placaVeiculo;

    @Indexed
    @NotNull
    @FutureOrPresent
    private LocalDateTime dataHoraInicio;

    @Indexed
    @NotNull
    private LocalDateTime dataHoraFim;

    private StatusAgendamento status = StatusAgendamento.AGENDADO;
    private String observacoes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getTipoServicoId() { return tipoServicoId; }
    public void setTipoServicoId(String tipoServicoId) { this.tipoServicoId = tipoServicoId; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
