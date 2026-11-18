package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.response.chuva.DadosChuva;
import br.com.fiap3ess.rotaseca.application.service.ChuvaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/chuva")
@Tag(name = "Chuva", description = "Intensidade da chuva atual e prevista (Open-Meteo)")
@RequiredArgsConstructor
public class ChuvaController {
    private final ChuvaService service;

    @GetMapping
    @Operation(summary = "Chuva atual e prevista (mm/h, de 15 em 15 min) em um ponto")
    public ResponseEntity<DadosChuva> consultar(
            @Parameter(example = "-23.6814075") @RequestParam BigDecimal lat,
            @Parameter(example = "-46.694591") @RequestParam BigDecimal lon) {
        return ResponseEntity.ok(service.consultar(lat, lon));
    }
}
