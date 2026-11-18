package br.com.fiap3ess.rotaseca.application.core.domain;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reporte de alagamento enviado pelo sensor de nível (ESP32), pelo app ou por parceiro
 * (equivale a "LeituraSensor" + "ReporteExterno" da proposta).
 */
@Getter
@AllArgsConstructor
public class Reporte {
    /** Acima disso a água derruba uma pessoa e esconde bueiro aberto (mesma faixa vermelha do ESP32). */
    public static final int NIVEL_INTRANSITAVEL_CM = 15;
    public static final int NIVEL_LAMINA_CM = 5;

    private Long id;
    private OrigemReporte origem;
    private String cruzamentoId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer nivelCm;
    private String descricao;
    private LocalDateTime recebidoEm;

    /** Faixa do nível da água, igual aos LEDs do sensor: seco, lâmina d'água ou intransitável a pé. */
    public String faixa() {
        if (nivelCm == null) {
            return null;
        }
        if (nivelCm >= NIVEL_INTRANSITAVEL_CM) {
            return "intransitavel_a_pe";
        }
        return nivelCm >= NIVEL_LAMINA_CM ? "lamina" : "seco";
    }
}
