package br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Item de GET /cruzamentos, no formato exato que o app mobile valida. */
public record DadosCruzamento(
        String id,
        String nome,
        @JsonProperty("nome_curto")
        String nomeCurto,
        String via,
        String bairro,
        Double latitude,
        Double longitude,
        boolean placeholder) {
}
