package br.com.fiap3ess.rotaseca.adapter.out.repository.persistence;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.ReporteEntity;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ReporteJpaRepository extends JpaRepository<ReporteEntity, Long> {
    boolean existsByOrigemAndCruzamentoIdAndRecebidoEmAfter(
            OrigemReporte origem, String cruzamentoId, LocalDateTime desde);

    boolean existsByOrigemAndCruzamentoIdIsNullAndLatitudeBetweenAndLongitudeBetweenAndRecebidoEmAfter(
            OrigemReporte origem, BigDecimal minLat, BigDecimal maxLat,
            BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde);
}
