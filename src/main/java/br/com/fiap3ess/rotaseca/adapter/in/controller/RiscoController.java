package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosLoteRisco;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosRisco;
import br.com.fiap3ess.rotaseca.application.service.RiscoService;
import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@Tag(name = "Risco", description = "Previsão de alagamento por cruzamento (contrato do app mobile)")
@RequiredArgsConstructor
public class RiscoController {
    private final RiscoService service;

    @GetMapping("/risco")
    @Operation(summary = "Previsão de um cruzamento (?local=), de vários (?locais=a,b) ou de uma área (?bbox=)",
            description = "Com 'em' (ISO 8601 com fuso), responde como teria respondido naquele instante. "
                    + "Horários sempre em -03:00; estimativa e limiar em fração (0.01083 = 1,083%).")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(oneOf = {DadosRisco.class, DadosLoteRisco.class, DadosRisco[].class})))
    public ResponseEntity<?> consultar(
            @Parameter(description = "Id do cruzamento", example = "nu-jaime")
            @RequestParam(required = false) String local,
            @Parameter(description = "Ids separados por vírgula (ex.: nu-jaime,cruzamento-2)")
            @RequestParam(required = false) String locais,
            @Parameter(description = "Área minLon,minLat,maxLon,maxLat; devolve a lista com o risco de cada cruzamento dentro dela "
                    + "(ex.: -46.75,-23.75,-46.60,-23.60)")
            @RequestParam(required = false) String bbox,
            @Parameter(description = "Instante da consulta", example = "2025-01-16T18:48:00-03:00")
            @RequestParam(required = false) String em) {
        if (local != null) {
            return ResponseEntity.ok(service.consultar(local, em));
        }
        if (locais != null) {
            return ResponseEntity.ok(service.consultarLote(locais, em));
        }
        if (bbox != null) {
            return ResponseEntity.ok(service.consultarArea(bbox, em));
        }
        throw new ParametroInvalidoException("Informe local, locais ou bbox.");
    }

    @GetMapping("/alertas")
    @Operation(summary = "Alertas de um cruzamento (previsões elevadas e confirmações do CGE) desde uma data")
    public ResponseEntity<List<DadosRisco>> alertas(
            @Parameter(example = "nu-jaime") @RequestParam String local,
            @Parameter(example = "2025-01-16T00:00:00-03:00") @RequestParam(required = false) String desde) {
        return ResponseEntity.ok(service.listarAlertas(local, desde));
    }
}
