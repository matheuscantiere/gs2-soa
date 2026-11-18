package br.com.fiap3ess.rotaseca.adapter.out.repository.mapper;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.ReporteEntity;
import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import org.springframework.stereotype.Component;

@Component
public class ReporteEntityMapper {
    public Reporte toDomain(ReporteEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Reporte(
                entity.getId(),
                entity.getOrigem(),
                entity.getCruzamentoId(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getNivelCm(),
                entity.getDescricao(),
                entity.getRecebidoEm()
        );
    }

    public ReporteEntity toEntity(Reporte domain) {
        return new ReporteEntity(
                domain.getId(),
                domain.getOrigem(),
                domain.getCruzamentoId(),
                domain.getLatitude(),
                domain.getLongitude(),
                domain.getNivelCm(),
                domain.getDescricao(),
                domain.getRecebidoEm()
        );
    }
}
