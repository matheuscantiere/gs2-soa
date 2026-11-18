package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosDetalhamentoReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosListagemReporte;
import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.NumeroMapper.numero;

@Component
public class ReporteMapper {
    /** descricao já chega sem dado pessoal (ver ReporteService). */
    public Reporte toDomain(DadosReporte dados, String descricaoLimpa, LocalDateTime recebidoEm) {
        String descricao = descricaoLimpa == null || descricaoLimpa.isBlank() ? null : descricaoLimpa.trim();
        String localId = dados.localId() == null || dados.localId().isBlank() ? null : dados.localId();
        return new Reporte(
                null,
                dados.origem(),
                localId,
                dados.latitude(),
                dados.longitude(),
                dados.nivelCm(),
                descricao,
                recebidoEm
        );
    }

    public DadosDetalhamentoReporte toDetails(Reporte domain) {
        return toDetails(domain, null);
    }

    public DadosDetalhamentoReporte toDetails(Reporte domain, Boolean dadosRemovidos) {
        return new DadosDetalhamentoReporte(
                domain.getId(),
                domain.getOrigem(),
                domain.getCruzamentoId(),
                numero(domain.getLatitude()),
                numero(domain.getLongitude()),
                domain.getNivelCm(),
                domain.faixa(),
                domain.getDescricao(),
                Horario.iso(domain.getRecebidoEm()),
                dadosRemovidos
        );
    }

    public DadosListagemReporte toList(Reporte domain) {
        return new DadosListagemReporte(
                domain.getId(),
                domain.getOrigem(),
                domain.getCruzamentoId(),
                domain.faixa(),
                Horario.iso(domain.getRecebidoEm())
        );
    }
}
