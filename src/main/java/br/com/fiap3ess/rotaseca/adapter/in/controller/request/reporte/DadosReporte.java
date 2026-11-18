package br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Reporte enviado pelo sensor ESP32, pelo app ou por parceiro. */
public record DadosReporte(
        @NotNull
        OrigemReporte origem,

        @JsonProperty("local_id")
        String localId,

        @NotNull
        @DecimalMin("-90") @DecimalMax("90")
        BigDecimal latitude,

        @NotNull
        @DecimalMin("-180") @DecimalMax("180")
        BigDecimal longitude,

        @Min(0) @Max(200)
        @JsonProperty("nivel_cm")
        Integer nivelCm,

        @Size(max = 280)
        String descricao) {
}
