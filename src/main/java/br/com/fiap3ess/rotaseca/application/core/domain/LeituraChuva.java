package br.com.fiap3ess.rotaseca.application.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Intensidade da chuva (mm/h) medida ou prevista sobre um cruzamento, coletada a cada 15 min. */
@Getter
@AllArgsConstructor
public class LeituraChuva {
    private Long id;
    private String cruzamentoId;
    private LocalDateTime medidoEm;
    private BigDecimal intensidadeMmH;
    private boolean prevista;
    private String fonte;
}
