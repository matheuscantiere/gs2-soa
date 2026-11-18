package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlagamentoRepository {
    /** Primeiro alagamento confirmado pelo CGE no cruzamento entre inicio e fim (inclusive). */
    Optional<Alagamento> primeiraConfirmacaoCge(String cruzamentoId, LocalDateTime inicio, LocalDateTime fim);

    List<Alagamento> listarConfirmacoesCgeDesde(String cruzamentoId, LocalDateTime desde);

    List<Alagamento> listarAtivosNaArea(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde);

    Alagamento save(Alagamento alagamento);
}
