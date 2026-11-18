package br.com.fiap3ess.rotaseca.adapter.out.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.PrevisaoEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.persistence.PrevisaoJpaRepository;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.application.port.out.PrevisaoRepository;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.StatusRisco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PrevisaoRepositoryImpl implements PrevisaoRepository {
    private final PrevisaoJpaRepository jpaRepository;
    private final PrevisaoEntityMapper entityMapper;

    @Override
    public Optional<Previsao> buscarVigente(String cruzamentoId, LocalDateTime instante) {
        return jpaRepository.buscarVigente(cruzamentoId, instante, instante.minusHours(1)).map(entityMapper::toDomain);
    }

    @Override
    public Optional<Previsao> buscarPorHora(String cruzamentoId, LocalDateTime previsaoHora) {
        return jpaRepository.findByCruzamentoIdAndPrevisaoHora(cruzamentoId, previsaoHora).map(entityMapper::toDomain);
    }

    @Override
    public List<Previsao> listarElevadasDesde(String cruzamentoId, LocalDateTime desde) {
        return jpaRepository
                .findAllByCruzamentoIdAndStatusAndPrevisaoHoraGreaterThanEqualOrderByPrevisaoHoraDesc(
                        cruzamentoId, StatusRisco.ELEVADO, desde)
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Previsao save(Previsao previsao) {
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(previsao)));
    }
}
