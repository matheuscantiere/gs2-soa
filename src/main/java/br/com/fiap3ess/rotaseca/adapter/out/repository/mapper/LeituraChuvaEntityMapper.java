package br.com.fiap3ess.rotaseca.adapter.out.repository.mapper;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.LeituraChuvaEntity;
import br.com.fiap3ess.rotaseca.application.core.domain.LeituraChuva;
import org.springframework.stereotype.Component;

@Component
public class LeituraChuvaEntityMapper {
    public LeituraChuva toDomain(LeituraChuvaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new LeituraChuva(
                entity.getId(),
                entity.getCruzamentoId(),
                entity.getMedidoEm(),
                entity.getIntensidadeMmH(),
                entity.isPrevista(),
                entity.getFonte()
        );
    }

    public LeituraChuvaEntity toEntity(LeituraChuva domain) {
        return new LeituraChuvaEntity(
                domain.getId(),
                domain.getCruzamentoId(),
                domain.getMedidoEm(),
                domain.getIntensidadeMmH(),
                domain.isPrevista(),
                domain.getFonte()
        );
    }
}
