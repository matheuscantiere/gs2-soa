package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrevisaoRepository {
    /** Previsão em vigor no instante: previsaoHora <= instante < previsaoHora + 1h. */
    Optional<Previsao> buscarVigente(String cruzamentoId, LocalDateTime instante);

    Optional<Previsao> buscarPorHora(String cruzamentoId, LocalDateTime previsaoHora);

    List<Previsao> listarElevadasDesde(String cruzamentoId, LocalDateTime desde);

    Previsao save(Previsao previsao);
}
