package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.alagamento.DadosRegistroAlagamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.alagamento.DadosAlagamento;
import br.com.fiap3ess.rotaseca.application.service.AlagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alagamentos")
@Tag(name = "Alagamentos", description = "Pontos de alagamento registrados pelo CGE")
@RequiredArgsConstructor
public class AlagamentoController {
    private final AlagamentoService service;

    @GetMapping
    @Operation(summary = "Alagamentos ativos dentro de uma área (bbox)")
    public ResponseEntity<List<DadosAlagamento>> listar(
            @Parameter(description = "minLon,minLat,maxLon,maxLat", example = "-46.75,-23.75,-46.60,-23.60")
            @RequestParam String bbox,
            @Parameter(description = "Padrão: últimas 24 h", example = "2025-01-16T00:00:00-03:00")
            @RequestParam(required = false) String desde) {
        return ResponseEntity.ok(service.listarNaArea(bbox, desde));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Registra a confirmação de um alagamento (carga do CGE)")
    public ResponseEntity<DadosAlagamento> registrar(@RequestBody @Valid DadosRegistroAlagamento dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }
}
