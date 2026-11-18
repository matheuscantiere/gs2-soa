package br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DadosAtualizacaoCruzamento(
        @NotBlank
        String id,

        @Size(max = 150)
        String nome,

        @Size(max = 100)
        @JsonProperty("nome_curto")
        String nomeCurto,

        @Size(max = 100)
        String via,

        @Size(max = 100)
        String bairro,

        @DecimalMin("-90") @DecimalMax("90")
        BigDecimal latitude,

        @DecimalMin("-180") @DecimalMax("180")
        BigDecimal longitude,

        Boolean placeholder,

        @PositiveOrZero
        Integer ordem) {
}
