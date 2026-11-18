package br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosDetalhamentoCruzamento(
        String id,
        String nome,
        @JsonProperty("nome_curto")
        String nomeCurto,
        String via,
        String bairro,
        Double latitude,
        Double longitude,
        boolean placeholder,
        int ordem,
        boolean ativo) {
}
