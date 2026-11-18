package br.com.fiap3ess.rotaseca.adapter.out.repository.mapper;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.CruzamentoEntity;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import org.springframework.stereotype.Component;

@Component
public class CruzamentoEntityMapper {
    public Cruzamento toDomain(CruzamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Cruzamento(
                entity.getId(),
                entity.getNome(),
                entity.getNomeCurto(),
                entity.getVia(),
                entity.getBairro(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.isPlaceholder(),
                entity.getOrdem(),
                entity.isAtivo()
        );
    }

    public CruzamentoEntity toEntity(Cruzamento domain) {
        return new CruzamentoEntity(
                domain.getId(),
                domain.getNome(),
                domain.getNomeCurto(),
                domain.getVia(),
                domain.getBairro(),
                domain.getLatitude(),
                domain.getLongitude(),
                domain.isPlaceholder(),
                domain.getOrdem(),
                domain.isAtivo()
        );
    }
}
