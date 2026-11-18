package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosDetalhamentoReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosListagemReporte;
import br.com.fiap3ess.rotaseca.application.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Reportes do sensor de nível (ESP32), do app e de parceiros")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService service;

    @PostMapping
    @Operation(summary = "Recebe um reporte de alagamento (público: sensor e app enviam sem login)",
            description = "Limite de 30 reportes por minuto por IP (HTTP 429). Telefone, CPF, e-mail e placa na "
                    + "descrição são trocados por [removido] (dados_removidos = true). Se a API estiver configurada "
                    + "com REPORTES_CHAVE_API, origem SENSOR/PARCEIRO exige o header X-Api-Key.")
    public ResponseEntity<DadosDetalhamentoReporte> receber(
            @RequestBody @Valid DadosReporte dados,
            @RequestHeader(name = "X-Api-Key", required = false) String chaveApi,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoReporte dto = service.receber(dados, chaveApi);
        URI uri = uriBuilder.path("/reportes/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Lista paginada dos reportes recebidos")
    public ResponseEntity<Page<DadosListagemReporte>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "recebidoEm", direction = Sort.Direction.DESC)
            Pageable paginacao) {
        return ResponseEntity.ok(service.listar(paginacao));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Detalha um reporte")
    public ResponseEntity<DadosDetalhamentoReporte> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalhar(id));
    }
}
