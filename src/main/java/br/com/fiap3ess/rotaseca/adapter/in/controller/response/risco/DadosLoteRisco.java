package br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Resposta de GET /risco?locais=a,b. */
public record DadosLoteRisco(
        List<DadosRisco> riscos,
        @JsonProperty("nao_encontrados")
        List<String> naoEncontrados) {
}
