package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento.DadosAtualizacaoCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento.DadosCadastroCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosDetalhamentoCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosListagemCruzamento;
import br.com.fiap3ess.rotaseca.application.port.in.StdFeaturesPort;
import br.com.fiap3ess.rotaseca.application.service.CruzamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cruzamentos")
@Tag(name = "Cruzamentos", description = "Cruzamentos cobertos pelo estudo (trechos monitorados)")
@RequiredArgsConstructor
public class CruzamentoController implements StdFeaturesPort<
        DadosCadastroCruzamento,
        DadosListagemCruzamento,
        DadosAtualizacaoCruzamento,
        Void,
        DadosDetalhamentoCruzamento,
        String> {
    private final CruzamentoService service;

    @GetMapping
    @Operation(summary = "Lista pública dos cruzamentos (usada pelo app mobile ao abrir)")
    public ResponseEntity<List<DadosCruzamento>> listarPublico() {
        return ResponseEntity.ok(service.listarPublico());
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Detalha um cruzamento")
    public ResponseEntity<DadosDetalhamentoCruzamento> detalhar(@PathVariable String id) {
        return ResponseEntity.ok(service.detalharCruzamento(id));
    }

    @Override
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Lista paginada de cruzamentos (administração)")
    public ResponseEntity<Page<DadosListagemCruzamento>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "ordem") Pageable paginacao) {
        return ResponseEntity.ok(service.listarCruzamentos(paginacao));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Cadastra um cruzamento")
    public ResponseEntity<DadosDetalhamentoCruzamento> cadastrar(
            @RequestBody @Valid DadosCadastroCruzamento dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoCruzamento dto = service.cadastrarCruzamento(dados);
        URI uri = uriBuilder.path("/cruzamentos/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Override
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualiza um cruzamento (campos nulos são ignorados)")
    public ResponseEntity<DadosDetalhamentoCruzamento> atualizar(@RequestBody @Valid DadosAtualizacaoCruzamento dados) {
        return ResponseEntity.ok(service.atualizarCruzamento(dados));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Exclusão lógica de um cruzamento")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluirCruzamento(id);
        return ResponseEntity.noContent().build();
    }
}
