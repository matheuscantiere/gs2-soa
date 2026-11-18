package br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosListagemCruzamento(
        String id,
        @JsonProperty("nome_curto")
        String nomeCurto,
        String bairro,
        boolean placeholder,
        int ordem) {
}
