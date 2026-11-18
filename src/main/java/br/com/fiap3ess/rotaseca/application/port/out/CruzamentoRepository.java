package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CruzamentoRepository {
    Page<Cruzamento> findAllByAtivoTrue(Pageable paginacao);

    List<Cruzamento> listarAtivosOrdenados();

    List<Cruzamento> listarAtivosComCoordenada();

    Optional<Cruzamento> buscarAtivo(String id);

    Optional<Cruzamento> findById(String id);

    boolean existsById(String id);

    Cruzamento save(Cruzamento cruzamento);
}
