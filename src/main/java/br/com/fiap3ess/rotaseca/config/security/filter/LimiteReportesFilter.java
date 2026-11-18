package br.com.fiap3ess.rotaseca.config.security.filter;

import br.com.fiap3ess.rotaseca.config.security.LimitadorTentativas;
import br.com.fiap3ess.rotaseca.config.security.RespostaErroJson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * POST /reportes é público (sensor e app enviam sem login): limita a quantidade por IP.
 * Conta toda requisição, inclusive as inválidas, para barrar enxurrada antes da validação.
 */
@Component
public class LimiteReportesFilter extends OncePerRequestFilter {
    private static final Duration JANELA = Duration.ofMinutes(1);

    private final LimitadorTentativas limitador;
    private final int limitePorMinuto;

    public LimiteReportesFilter(
            LimitadorTentativas limitador,
            @Value("${rotaseca.limite.reportes-por-minuto}") int limitePorMinuto) {
        this.limitador = limitador;
        this.limitePorMinuto = limitePorMinuto;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        return !("POST".equals(request.getMethod()) && "/reportes".equals(caminho));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String chave = "reporte:" + request.getRemoteAddr();
        if (limitador.registrar(chave, JANELA) > limitePorMinuto) {
            response.setHeader("Retry-After", String.valueOf(limitador.segundosRestantes(chave)));
            RespostaErroJson.escrever(response, HttpStatus.TOO_MANY_REQUESTS, "muitas_tentativas",
                    "Limite de " + limitePorMinuto + " reportes por minuto atingido. Tente novamente em instantes.");
            return;
        }
        chain.doFilter(request, response);
    }
}
