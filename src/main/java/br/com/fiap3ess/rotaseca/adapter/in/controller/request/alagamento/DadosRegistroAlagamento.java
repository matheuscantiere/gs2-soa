package br.com.fiap3ess.rotaseca.adapter.in.controller.request.alagamento;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DadosRegistroAlagamento(
        @NotBlank
        @JsonProperty("local_id")
        String localId,

        @NotBlank
        @JsonProperty("registrado_em")
        String registradoEm,

        Boolean intransitavel,

        /** Padrão: CGE. */
        FonteAlagamento fonte) {
}
