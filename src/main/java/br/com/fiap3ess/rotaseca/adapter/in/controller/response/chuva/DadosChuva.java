package br.com.fiap3ess.rotaseca.adapter.in.controller.response.chuva;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DadosChuva(
        Double latitude,
        Double longitude,
        @JsonProperty("medido_em")
        String medidoEm,
        @JsonProperty("intensidade_atual_mm_h")
        Double intensidadeAtualMmH,
        List<DadosPontoChuva> prevista,
        String fonte) {

    public record DadosPontoChuva(
            String hora,
            @JsonProperty("intensidade_mm_h")
            Double intensidadeMmH) {
    }
}
