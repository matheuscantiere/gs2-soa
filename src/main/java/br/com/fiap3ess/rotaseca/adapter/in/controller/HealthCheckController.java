package br.com.fiap3ess.rotaseca.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health check")
public class HealthCheckController {
    @GetMapping
    @Operation(summary = "Verificação de integridade da API")
    public String healthCheck() {
        return "Rota Seca - Serviço de Risco da Rua: ok";
    }
}
