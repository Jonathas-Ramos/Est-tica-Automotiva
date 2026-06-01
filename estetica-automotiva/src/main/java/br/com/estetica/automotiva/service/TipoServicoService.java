package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.TipoServico;
import br.com.estetica.automotiva.repository.TipoServicoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TipoServicoService {
    private final TipoServicoRepository repository;

    public TipoServicoService(TipoServicoRepository repository) {
        this.repository = repository;
    }

    public TipoServico salvar(TipoServico tipoServico) {
        limparIdVazio(tipoServico);
        return repository.save(tipoServico);
    }

    public List<TipoServico> listarAtivos() {
        corrigirServicosComIdVazio();
        return repository.findByAtivoTrueOrderByNomeAsc();
    }

    public List<TipoServico> listarTodos() {
        corrigirServicosComIdVazio();
        return repository.findAll();
    }

    public TipoServico buscar(String id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Servico nao encontrado")); }

    private void limparIdVazio(TipoServico tipoServico) {
        if (tipoServico.getId() != null && tipoServico.getId().isBlank()) {
            tipoServico.setId(null);
        }
    }

    private void corrigirServicosComIdVazio() {
        List<TipoServico> corrigidos = new ArrayList<>();
        for (TipoServico servico : repository.findAll()) {
            if (servico.getId() != null && servico.getId().isBlank()) {
                repository.delete(servico);
                servico.setId(null);
                corrigidos.add(servico);
            }
        }
        if (!corrigidos.isEmpty()) {
            repository.saveAll(corrigidos);
        }
    }
}
