package br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta de GET /risco?local= (contrato do app mobile). Horários em ISO 8601 com -03:00,
 * estimativa e limiar em fração (0.01083 = 1,083%). estimativa e confirmado_em saem como null explícito.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record DadosRisco(
        @JsonProperty("local_id")
        String localId,
        String cruzamento,
        @JsonProperty("previsao_hora")
        String previsaoHora,
        @JsonProperty("valida_ate")
        String validaAte,
        Double estimativa,
        Double limiar,
        String status,
        @JsonProperty("confirmado_cge")
        boolean confirmadoCge,
        @JsonProperty("confirmado_em")
        String confirmadoEm) {
}
