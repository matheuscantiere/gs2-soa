package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReporteRepository {
    Reporte save(Reporte reporte);

    Page<Reporte> findAll(Pageable paginacao);

    Optional<Reporte> findById(Long id);

    boolean existeRecente(OrigemReporte origem, String cruzamentoId, LocalDateTime desde);

    /** Reporte da mesma origem, sem cruzamento, dentro da caixa (lat/lon) desde o instante. */
    boolean existeRecentePerto(OrigemReporte origem, BigDecimal minLat, BigDecimal maxLat,
                               BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde);
}
