package br.com.estetica.automotiva.repository;

import br.com.estetica.automotiva.model.TipoServico;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TipoServicoRepository extends MongoRepository<TipoServico, String> {
    List<TipoServico> findByAtivoTrueOrderByNomeAsc();
}
