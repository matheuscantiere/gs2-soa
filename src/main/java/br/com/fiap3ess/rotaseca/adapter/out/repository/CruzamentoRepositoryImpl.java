package br.com.fiap3ess.rotaseca.adapter.out.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.CruzamentoEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.persistence.CruzamentoJpaRepository;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CruzamentoRepositoryImpl implements CruzamentoRepository {
    private final CruzamentoJpaRepository jpaRepository;
    private final CruzamentoEntityMapper entityMapper;

    @Override
    public Page<Cruzamento> findAllByAtivoTrue(Pageable paginacao) {
        return jpaRepository.findAllByAtivoTrue(paginacao).map(entityMapper::toDomain);
    }

    @Override
    public List<Cruzamento> listarAtivosOrdenados() {
        return jpaRepository.findAllByAtivoTrueOrderByOrdemAscIdAsc().stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public List<Cruzamento> listarAtivosComCoordenada() {
        return jpaRepository.findAllByAtivoTrueAndLatitudeNotNullAndLongitudeNotNull()
                .stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Optional<Cruzamento> buscarAtivo(String id) {
        return jpaRepository.findByIdAndAtivoTrue(id).map(entityMapper::toDomain);
    }

    @Override
    public Optional<Cruzamento> findById(String id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Cruzamento save(Cruzamento cruzamento) {
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(cruzamento)));
    }
}
