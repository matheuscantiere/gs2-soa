package br.com.fiap3ess.rotaseca.adapter.out.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.AlagamentoEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.persistence.AlagamentoJpaRepository;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.port.out.AlagamentoRepository;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlagamentoRepositoryImpl implements AlagamentoRepository {
    private final AlagamentoJpaRepository jpaRepository;
    private final AlagamentoEntityMapper entityMapper;

    @Override
    public Optional<Alagamento> primeiraConfirmacaoCge(String cruzamentoId, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository
                .findFirstByCruzamentoIdAndFonteAndAtivoTrueAndRegistradoEmBetweenOrderByRegistradoEmAsc(
                        cruzamentoId, FonteAlagamento.CGE, inicio, fim)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Alagamento> listarConfirmacoesCgeDesde(String cruzamentoId, LocalDateTime desde) {
        return jpaRepository
                .findAllByCruzamentoIdAndFonteAndAtivoTrueAndRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(
                        cruzamentoId, FonteAlagamento.CGE, desde)
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public List<Alagamento> listarAtivosNaArea(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLon, BigDecimal maxLon, LocalDateTime desde) {
        return jpaRepository.buscarAtivosNaArea(minLat, maxLat, minLon, maxLon, desde)
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Alagamento save(Alagamento alagamento) {
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(alagamento)));
    }
}
