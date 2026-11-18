package br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Uma previsão do modelo de ML: probabilidade (0 a 1) de alagamento no cruzamento naquela hora. */
public record DadosPrevisao(
        @NotBlank
        @JsonProperty("local_id")
        String localId,

        @NotBlank
        @JsonProperty("previsao_hora")
        String previsaoHora,

        @NotNull
        @DecimalMin("0") @DecimalMax("1")
        BigDecimal estimativa,

        @DecimalMin("0") @DecimalMax("1")
        BigDecimal limiar) {
}
