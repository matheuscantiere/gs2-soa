package br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosDetalhamentoReporte(
        Long id,
        OrigemReporte origem,
        @JsonProperty("local_id")
        String localId,
        Double latitude,
        Double longitude,
        @JsonProperty("nivel_cm")
        Integer nivelCm,
        String faixa,
        String descricao,
        @JsonProperty("recebido_em")
        String recebidoEm,
        /** Só no POST: true quando o texto tinha dado pessoal e ele foi removido antes de gravar. */
        @JsonProperty("dados_removidos")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean dadosRemovidos) {
}
