package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import br.com.fiap3ess.rotaseca.adapter.in.controller.response.alagamento.DadosAlagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import org.springframework.stereotype.Component;

import static br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.NumeroMapper.numero;

@Component
public class AlagamentoMapper {
    public DadosAlagamento toDados(Alagamento alagamento, Cruzamento cruzamento) {
        return new DadosAlagamento(
                alagamento.getId(),
                alagamento.getCruzamentoId(),
                cruzamento.getNome(),
                numero(cruzamento.getLatitude()),
                numero(cruzamento.getLongitude()),
                Horario.iso(alagamento.getRegistradoEm()),
                alagamento.getFonte(),
                alagamento.isIntransitavel()
        );
    }
}
