package br.com.fiap3ess.rotaseca.adapter.in.controller.response.alagamento;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosAlagamento(
        Long id,
        @JsonProperty("local_id")
        String localId,
        String cruzamento,
        Double latitude,
        Double longitude,
        @JsonProperty("registrado_em")
        String registradoEm,
        FonteAlagamento fonte,
        boolean intransitavel) {
}
