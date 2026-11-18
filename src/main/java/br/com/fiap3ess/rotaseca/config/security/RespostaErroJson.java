package br.com.fiap3ess.rotaseca.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Erros gerados nos filtros de segurança (antes dos controllers), no mesmo formato {erro:{codigo,mensagem}}. */
public final class RespostaErroJson {
    private RespostaErroJson() {
    }

    public static void escrever(HttpServletResponse response, HttpStatus status, String codigo, String mensagem)
            throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"erro\":{\"codigo\":\"" + codigo + "\",\"mensagem\":\""
                + mensagem.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}}");
    }
}
