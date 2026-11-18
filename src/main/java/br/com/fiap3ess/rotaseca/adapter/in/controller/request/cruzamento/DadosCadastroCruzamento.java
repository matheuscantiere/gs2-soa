package br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DadosCadastroCruzamento(
        @NotBlank
        @Pattern(regexp = "^[a-z0-9-]{3,40}$", message = "use de 3 a 40 letras minúsculas, números ou hífen")
        String id,

        @NotBlank
        @Size(max = 150)
        String nome,

        @NotBlank
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

        @NotNull
        Boolean placeholder,

        @PositiveOrZero
        Integer ordem) {
}
