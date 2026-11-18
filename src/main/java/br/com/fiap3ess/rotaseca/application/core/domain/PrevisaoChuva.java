package br.com.fiap3ess.rotaseca.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Chuva atual e prevista (próximas horas, de 15 em 15 min) em um ponto, vinda de provedor externo. */
public record PrevisaoChuva(
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime medidoEm,
        BigDecimal intensidadeAtualMmH,
        List<Ponto> prevista,
        String fonte) {

    public record Ponto(LocalDateTime hora, BigDecimal intensidadeMmH) {
    }
}
