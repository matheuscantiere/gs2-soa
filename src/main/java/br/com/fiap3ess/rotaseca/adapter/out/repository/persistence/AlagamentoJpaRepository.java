package br.com.fiap3ess.rotaseca.adapter.out.repository.persistence;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.AlagamentoEntity;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlagamentoJpaRepository extends JpaRepository<AlagamentoEntity, Long> {
    Optional<AlagamentoEntity> findFirstByCruzamentoIdAndFonteAndAtivoTrueAndRegistradoEmBetweenOrderByRegistradoEmAsc(
            String cruzamentoId, FonteAlagamento fonte, LocalDateTime inicio, LocalDateTime fim);

    List<AlagamentoEntity> findAllByCruzamentoIdAndFonteAndAtivoTrueAndRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(
            String cruzamentoId, FonteAlagamento fonte, LocalDateTime desde);

    /** Alagamentos ativos em cruzamentos dentro da caixa (bbox). Sem PostGIS: BETWEEN em lat/lon. */
    @Query("""
        select a from Alagamento a, Cruzamento c
        where
        c.id = a.cruzamentoId
        and
        a.ativo = true
        and
        a.registradoEm >= :desde
        and
        c.latitude between :minLat and :maxLat
        and
        c.longitude between :minLon and :maxLon
        order by a.registradoEm desc
    """)
    List<AlagamentoEntity> buscarAtivosNaArea(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde);
}
