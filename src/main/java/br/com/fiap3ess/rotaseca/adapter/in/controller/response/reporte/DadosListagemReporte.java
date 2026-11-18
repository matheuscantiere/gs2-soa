package br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosListagemReporte(
        Long id,
        OrigemReporte origem,
        @JsonProperty("local_id")
        String localId,
        String faixa,
        @JsonProperty("recebido_em")
        String recebidoEm) {
}
