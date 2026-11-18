package br.com.fiap3ess.rotaseca.adapter.out.repository.mapper;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.AlagamentoEntity;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import org.springframework.stereotype.Component;

@Component
public class AlagamentoEntityMapper {
    public Alagamento toDomain(AlagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Alagamento(
                entity.getId(),
                entity.getCruzamentoId(),
                entity.getRegistradoEm(),
                entity.getFonte(),
                entity.isIntransitavel(),
                entity.isAtivo()
        );
    }

    public AlagamentoEntity toEntity(Alagamento domain) {
        return new AlagamentoEntity(
                domain.getId(),
                domain.getCruzamentoId(),
                domain.getRegistradoEm(),
                domain.getFonte(),
                domain.isIntransitavel(),
                domain.isAtivo()
        );
    }
}
