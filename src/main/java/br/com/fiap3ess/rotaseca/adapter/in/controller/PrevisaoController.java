package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosLotePrevisao;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosResultadoPrevisoes;
import br.com.fiap3ess.rotaseca.application.service.PrevisaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/previsoes")
@Tag(name = "Previsões", description = "Entrada das probabilidades calculadas pelo modelo de ML")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class PrevisaoController {
    private final PrevisaoService service;

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Grava (ou atualiza) um lote de previsões horárias por cruzamento")
    public ResponseEntity<DadosResultadoPrevisoes> gravar(@RequestBody @Valid DadosLotePrevisao dados) {
        return ResponseEntity.ok(service.gravarLote(dados));
    }
}
