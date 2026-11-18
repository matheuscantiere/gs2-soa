package br.com.fiap3ess.rotaseca.adapter.out.repository.persistence;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.PrevisaoEntity;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.StatusRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrevisaoJpaRepository extends JpaRepository<PrevisaoEntity, Long> {
    /** Vigente no instante: previsaoHora <= instante e previsaoHora > instante - 1h. */
    @Query("""
        select p from Previsao p
        where
        p.cruzamentoId = :cruzamentoId
        and
        p.previsaoHora <= :instante
        and
        p.previsaoHora > :umaHoraAntes
        order by p.previsaoHora desc
        limit 1
    """)
    Optional<PrevisaoEntity> buscarVigente(String cruzamentoId, LocalDateTime instante, LocalDateTime umaHoraAntes);

    Optional<PrevisaoEntity> findByCruzamentoIdAndPrevisaoHora(String cruzamentoId, LocalDateTime previsaoHora);

    List<PrevisaoEntity> findAllByCruzamentoIdAndStatusAndPrevisaoHoraGreaterThanEqualOrderByPrevisaoHoraDesc(
            String cruzamentoId, StatusRisco status, LocalDateTime desde);
}
