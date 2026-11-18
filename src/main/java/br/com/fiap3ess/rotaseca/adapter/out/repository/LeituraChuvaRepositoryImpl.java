package br.com.fiap3ess.rotaseca.adapter.out.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.LeituraChuvaEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.persistence.LeituraChuvaJpaRepository;
import br.com.fiap3ess.rotaseca.application.core.domain.LeituraChuva;
import br.com.fiap3ess.rotaseca.application.port.out.LeituraChuvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeituraChuvaRepositoryImpl implements LeituraChuvaRepository {
    private final LeituraChuvaJpaRepository jpaRepository;
    private final LeituraChuvaEntityMapper entityMapper;

    @Override
    public LeituraChuva save(LeituraChuva leitura) {
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(leitura)));
    }
}
