package br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DadosLotePrevisao(
        @NotBlank
        @Size(max = 30)
        String fonte,

        @NotEmpty
        @Valid
        List<DadosPrevisao> previsoes) {
}
