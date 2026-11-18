package br.com.fiap3ess.rotaseca.adapter.out.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.ReporteEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.persistence.ReporteJpaRepository;
import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import br.com.fiap3ess.rotaseca.application.port.out.ReporteRepository;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReporteRepositoryImpl implements ReporteRepository {
    private final ReporteJpaRepository jpaRepository;
    private final ReporteEntityMapper entityMapper;

    @Override
    public Reporte save(Reporte reporte) {
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(reporte)));
    }

    @Override
    public Page<Reporte> findAll(Pageable paginacao) {
        return jpaRepository.findAll(paginacao).map(entityMapper::toDomain);
    }

    @Override
    public Optional<Reporte> findById(Long id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public boolean existeRecente(OrigemReporte origem, String cruzamentoId, LocalDateTime desde) {
        return jpaRepository.existsByOrigemAndCruzamentoIdAndRecebidoEmAfter(origem, cruzamentoId, desde);
    }

    @Override
    public boolean existeRecentePerto(OrigemReporte origem, BigDecimal minLat, BigDecimal maxLat,
                                      BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde) {
        return jpaRepository.existsByOrigemAndCruzamentoIdIsNullAndLatitudeBetweenAndLongitudeBetweenAndRecebidoEmAfter(
                origem, minLat, maxLat, minLon, maxLon, desde);
    }
}
