package br.com.fiap3ess.rotaseca.adapter.out.repository.mapper;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.PrevisaoEntity;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import org.springframework.stereotype.Component;

@Component
public class PrevisaoEntityMapper {
    public Previsao toDomain(PrevisaoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Previsao(
                entity.getId(),
                entity.getCruzamentoId(),
                entity.getPrevisaoHora(),
                entity.getEstimativa(),
                entity.getLimiar(),
                entity.getStatus(),
                entity.getFonte()
        );
    }

    public PrevisaoEntity toEntity(Previsao domain) {
        return new PrevisaoEntity(
                domain.getId(),
                domain.getCruzamentoId(),
                domain.getPrevisaoHora(),
                domain.getEstimativa(),
                domain.getLimiar(),
                domain.getStatus(),
                domain.getFonte()
        );
    }
}
